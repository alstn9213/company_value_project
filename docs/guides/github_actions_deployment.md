GitHub Actions를 사용하면 코드를 깃허브에 `push`하는 것만으로 빌드부터 배포까지 자동으로 처리할 수 있습니다. 이번 포스팅에서는 GCP(Google Cloud Platform) VM 인스턴스에 Spring Boot와 React가 합쳐진 프로젝트를 Docker로 배포하는 과정을 정리해 보았습니다.

---

## 1단계: Spring Boot 설정 수정 (`application.properties`)

배포할 때마다 DB 비밀번호나 API 키가 바뀌거나, 보안상 코드에 직접 적어두면 안 되는 값들이 있습니다. 이를 환경 변수로 주입받을 수 있도록 `application.properties`를 수정해야 합니다.

`src/main/resources/application.properties` 파일을 열어 민감한 정보들을 `${환경변수명}` 형태로 바꿔줍니다.

```properties
# ==========================================
# [Database]
# ==========================================
# 로컬에서 돌릴 땐 기본값(jdbc:mariadb://...)을 쓰고, 배포 시엔 환경변수(SPRING_DATASOURCE_URL)를 씁니다.
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:mariadb://localhost:3306/value}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:root}
# 비밀번호는 기본값 없이 무조건 환경변수로 받도록 설정
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}

# ==========================================
# [Redis]
# ==========================================
spring.data.redis.host=${SPRING_DATA_REDIS_HOST:localhost}
spring.data.redis.port=${SPRING_DATA_REDIS_PORT:6379}

# ==========================================
# [External APIs & Security]
# ==========================================
# API 키와 JWT 비밀키도 환경변수로 처리
api.alpha-vantage.key=${API_ALPHAVANTAGE_KEY}
api.fred.key=${API_FRED_KEY}
jwt.secret=${JWT_SECRET_KEY}

```

이렇게 해두면 나중에 Docker 컨테이너를 실행할 때 외부에서 진짜 값을 넣어줄 수 있습니다.

---

## 2단계: 서버 준비 (GCP Compute Engine)

배포할 컴퓨터(서버)를 준비합니다.

1. **GCP 콘솔** -> **Compute Engine** -> **VM 인스턴스** -> **인스턴스 만들기**.
2. **설정 (무료 등급 기준)**:
* **리전**: `us-west1` (오리건) 등 *미국 리전* (e2-micro 무료 혜택).
* **머신 유형**: `e2-micro`.
* **부팅 디스크**: Ubuntu 22.04 LTS.
* **방화벽**: `HTTP`, `HTTPS` 허용 체크.


3. **만들기** 클릭 후, **VPC 네트워크**에서 IP를 고정 IP로 예약하는 것을 추천합니다.
4. **포트 열기**: **VPC 네트워크** -> **방화벽**에서 `tcp:8080` 포트 허용 규칙을 추가합니다.

---

## 3단계: 서버 환경 설정 (Docker, DB)

서버에 SSH로 접속하여 Docker를 설치하고, 데이터베이스를 미리 실행해 둡니다.

```bash
# 1. Docker 설치
sudo apt-get update
sudo apt-get install -y docker.io
sudo usermod -aG docker $USER
# (설치 후 exit 했다가 다시 접속해야 권한 적용됨)

# 2. MariaDB & Redis 실행 (사전 준비)
# 배포 스크립트는 '앱'만 교체하므로, DB는 미리 띄워놔야 합니다.
docker run -d --name company_mariadb -p 3306:3306 -e MARIADB_ROOT_PASSWORD=내비밀번호 mariadb:10.6
docker run -d --name company_redis -p 6379:6379 redis:alpine

```

---

## 4단계: GitHub Secrets 등록

이제 1단계에서 비워둔 변수들의 '실제 값'을 깃허브에 저장할 차례입니다. 코드를 공개하더라도 이 비밀번호들은 안전하게 숨겨집니다.

1. GitHub 저장소의 **Settings** -> **Secrets and variables** -> **Actions** 탭으로 이동.
2. **New repository secret** 버튼을 눌러 아래 변수들을 등록합니다.

| Secret 이름 (Key) | 값 (Value) 예시 | 설명 |
| --- | --- | --- |
| `DOCKER_USERNAME` | `docker1234` | Docker Hub 아이디 |
| `DOCKER_PASSWORD` | `********` | Docker Hub 비밀번호 |
| `SERVER_HOST` | `34.64.x.x` | GCP VM의 외부 IP |
| `SSH_PRIVATE_KEY` | `-----BEGIN...` | 로컬에서 만든 SSH 개인키 전체 내용 |
| `VM_USERNAME` | `vm1234` | VM 접속 사용자명 (SSH 키 만들 때 계정) |
| `DB_PASSWORD` | `********` | 3단계에서 설정한 마리아DB 비밀번호 |
| `JWT_SECRET` | `my_secret_key...` | JWT 토큰 생성용 임의의 문자열 |
| `ALPHA_API_KEY` | `A1B2...` | Alpha Vantage API 키 |
| `FRED_API_KEY` | `abcdef...` | FRED API 키 |

*여기서 등록한 이름(`DB_PASSWORD` 등)이 워크플로우 파일에서 `${{ secrets.DB_PASSWORD }}` 형태로 사용됩니다.*

---

## 5단계: 워크플로우 작성 (`.github/workflows/deploy-ssh.yml`)

마지막으로 이 모든 것을 연결하는 자동 배포 스크립트를 작성합니다.
`docker run` 명령어 부분을 보면, **GitHub Secrets에 저장된 값을 꺼내서 → 스프링 부트의 환경 변수(`-e`)로 넣어주는 과정**을 볼 수 있습니다.

```yaml
name: Deploy to VM via SSH

on:
  push:
    branches: [ "main" ]

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      # 1. Java 17 세팅
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      # 2. Node.js 세팅
      - name: Set up Node.js
        uses: actions/setup-node@v4
        with:
          node-version: '20'

      # 프론트엔드 빌드 & 백엔드로 복사(React 빌드 결과물을 Spring Boot static 폴더로 이동)
      - name: Build Frontend & Copy
        working-directory: ./FRONT/companyvalue
        run: |
          npm ci
          npm run build
          mkdir -p ../../BACK/companyvalue/src/main/resources/static
          cp -r dist/* ../../BACK/companyvalue/src/main/resources/static/

      # 백엔드 빌드 (Gradle)
      - name: Build Backend
        working-directory: ./BACK/companyvalue
        run: |
          chmod +x gradlew
          ./gradlew clean build -x test

      # Docker 이미지 빌드 & 푸시
      - name: Build and Push Docker Image
        uses: docker/build-push-action@v5
        with:
          context: ./BACK/companyvalue
          push: true
          tags: ${{ secrets.DOCKER_USERNAME }}/value-pick:latest
          username: ${{ secrets.DOCKER_USERNAME }}
          password: ${{ secrets.DOCKER_PASSWORD }}

      # 서버 배포 (SSH)
      - name: Deploy to Server via SSH
        uses: appleboy/ssh-action@v1.0.3
        with:
          host: ${{ secrets.SERVER_HOST }}
          username: ${{ secrets.VM_USERNAME }}
          key: ${{ secrets.SSH_PRIVATE_KEY }}
          script: |
            docker pull ${{ secrets.DOCKER_USERNAME }}/value-pick:latest
            docker stop app-server || true
            docker rm app-server || true
            
            # ★ 여기서 Secrets 값을 컨테이너의 환경 변수로 주입합니다 ★
            docker run -d \
              --name app-server \
              --network host \
              -e SPRING_DATASOURCE_URL="jdbc:mariadb://127.0.0.1:3306/value" \
              -e SPRING_DATASOURCE_USERNAME="root" \
              -e SPRING_DATASOURCE_PASSWORD="${{ secrets.DB_PASSWORD }}" \
              -e JWT_SECRET_KEY="${{ secrets.JWT_SECRET }}" \
              -e API_ALPHAVANTAGE_KEY="${{ secrets.ALPHA_API_KEY }}" \
              -e API_FRED_KEY="${{ secrets.FRED_API_KEY }}" \
              ${{ secrets.DOCKER_USERNAME }}/value-pick:latest
            
            docker image prune -f

```

**주의할 점:**

* `SPRING_DATASOURCE_USERNAME`: 실제 DB 사용자명과 일치해야 합니다. (제 경우 `root`)
* `--network host`: 이 옵션을 쓰면 컨테이너가 호스트의 포트(DB:3306 등)에 `127.0.0.1`로 바로 접근할 수 있어 설정이 간편합니다.
* `jdbc:mariadb://127.0.0.1:3306/value`: 서버에 설치된 MariaDB 포트가 3306이어야 합니다.
* '/' 뒤에는 공백이 없어야합니다. 

---

## 6단계: 배포 확인

이제 코드를 `main` 브랜치에 푸시하면:

1. GitHub Actions가 코드를 빌드하고 Docker 이미지를 만듭니다.
2. GCP 서버에 접속해서 새 이미지를 다운받습니다.
3. `application.properties`의 변수 자리에 GitHub Secrets의 실제 값을 채워 넣으며 컨테이너를 실행합니다.

잠시 후 브라우저로 서버 IP(`http://내-서버-IP:8080`)에 접속해 보면 배포된 프로젝트를 확인할 수 있습니다!
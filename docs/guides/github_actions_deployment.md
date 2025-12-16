# 깃허브 액션 자동 배포

github actions를 사용하면 깃허브에 push할 때 배포까지 자동으로 됩니다.

-----

### 1단계: 서버 준비 (GCP Compute Engine)

가장 먼저 배포할 컴퓨터(서버)가 있어야 합니다.

1.  **GCP 콘솔** \> **Compute Engine** \> **VM 인스턴스** \> **인스턴스 만들기**.
2.  **설정 (무료 등급 기준)**:
      * **리전**: `us-west1` (오리건) 등 *미국 리전*을 선택해야 e2-micro 무료 혜택을 받기 쉽습니다. (서울 리전은 유료지만 속도가 빠릅니다. 선택은 자유입니다.)
      * **머신 유형**: `e2-micro` (공유 코어, 메모리 1GB).
      * **부팅 디스크**: Ubuntu 22.04 LTS (추천).
      * **방화벽**: `HTTP 트래픽 허용`, `HTTPS 트래픽 허용` 체크.
3.  **만들기** 클릭.
4.  **고정 IP 설정 (선택)**: 서버를 껐다 켜도 주소가 안 바뀌게 하려면 **VPC 네트워크** \> **IP 주소**에서 해당 VM의 IP를 '고정'으로 예약하세요.
5.  **포트 열기**: 8080포트로 접속해야 하므로, **VPC 네트워크** \> **방화벽** \> **방화벽 규칙 만들기**에서 `tcp:8080`을 허용하는 규칙을 추가해야 합니다. (대상 태그를 지정하고 VM에도 같은 태그를 달아주세요.)

### 2단계: 서버에 Docker 설치

만들어진 서버에 SSH 버튼을 눌러 접속한 뒤, 도커를 설치합니다.

```bash
# 서버 터미널에서 실행
sudo apt-get update
sudo apt-get install -y docker.io
sudo usermod -aG docker $USER
# (설치 후 exit 명령어로 나갔다가 다시 SSH 접속해야 권한 적용됨)
```

### 3단계: GitHub Secrets 설정

GitHub가 내 서버에 로그인하고, Docker Hub에 이미지를 올릴 수 있도록 열쇠를 줍니다.

1.  **[Docker Hub](https://hub.docker.com/)** 회원가입 후 **Create Repository** 클릭 (이름 예: `value-pick`, Public/Private 상관없음).
2.  **SSH 키 생성**: 내 컴퓨터(로컬)에서 SSH 키 쌍을 만듭니다.
      * 내 로컬 컴퓨터의 powershell에서 명령어 `ssh-keygen -t rsa -b 4096 -f my-key`를 치고 파일을 다운받습니다.
      * `my-key`(개인키)와 `my-key.pub`(공개키)가 생깁니다.
      * **서버 등록**: `my-key.pub`의 내용을 복사해서, GCP VM 인스턴스 상세 정보의 **"SSH 키"** 항목에 추가하고 저장합니다. 
3. **서버(GCP VM)에 공개키 등록하기**

```bash
# VM 접속 후
nano ~/.ssh/authorized_keys
# 복사한 공개키 붙여넣기 후 저장 (ctrl+o, ctrl+x)
chmod 600 ~/.ssh/authorized_keys # 권한 설정 중요
chmod 700 ~/.ssh
```

3.  **GitHub 저장소 Settings** \> **Secrets** \> **Actions**에 다음 변수들을 등록합니다.
      * `DOCKER_USERNAME`: Docker Hub 아이디
      * `DOCKER_PASSWORD`: Docker Hub 비밀번호
      * `SERVER_HOST`: 내 서버 IP 주소 (예: 34.12.34.56)
      * `SSH_PRIVATE_KEY`: 아까 만든 `my-key` 파일의 **전체 내용** (개인키)
      * `VM_USERNAME`: vm ssh로 들어가서 뜨는 사용자 Id
      * `API_KEY`: 프로젝트에서 사용된 각종 api 키를 각각 등록해야함

### 4단계: 워크플로우 작성 (`.github/workflows/deploy-ssh.yml`)


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

      # 3. 프론트엔드 빌드 & 통합
      - name: Build Frontend & Copy
        working-directory: ./FRONT/companyvalue
        run: |
          npm ci
          npm run build
          mkdir -p ../../BACK/companyvalue/src/main/resources/static
          cp -r dist/* ../../BACK/companyvalue/src/main/resources/static/

      # 4. 백엔드(Spring Boot) 빌드
      - name: Build Backend (Gradle)
        working-directory: ./BACK/companyvalue
        run: |
          chmod +x gradlew
          ./gradlew clean build -x test

      # 5. Docker Hub 로그인
      - name: Login to Docker Hub
        uses: docker/login-action@v3
        with:
          username: ${{ secrets.DOCKER_USERNAME }}
          password: ${{ secrets.DOCKER_PASSWORD }}

      # 6. Docker 이미지 빌드 & Push
      - name: Build and Push Docker Image
        uses: docker/build-push-action@v5
        with:
          context: ./BACK/companyvalue
          push: true
          tags: ${{ secrets.DOCKER_USERNAME }}/value-pick:latest

      # 7. 서버에 SSH 접속해서 배포 명령 실행
      - name: Deploy to Server via SSH
        uses: appleboy/ssh-action@v1.0.3
        with:
          host: ${{ secrets.SERVER_HOST }}
          username: ${{ secrets.VM_USERNAME }} # GCP VM의 사용자명 (보통 구글계정 아이디 앞부분)
          key: ${{ secrets.SSH_PRIVATE_KEY }}
          # / 뒤에 공백이 있으면 오류난다.
          script: |
            # 최신 이미지 다운로드
            docker pull ${{ secrets.DOCKER_USERNAME }}/value-pick:latest
            
            # 기존 컨테이너가 있으면 중지하고 삭제
            docker stop app-server || true
            docker rm app-server || true
            
            # 새 컨테이너 실행 (환경변수 주입)
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
            
            # 불필요한 이미지 정리
            docker image prune -f
```

### 핵심 포인트

1.  **Docker Hub 활용**: GitHub Actions에서 만든 결과물(이미지)을 Docker Hub라는 '중간 창고'에 보관합니다.
2.  **SSH Action**: `appleboy/ssh-action`이라는 라이브러리를 써서, 마치 님이 집에서 터미널로 접속하듯 GitHub가 서버에 접속합니다.
3.  **Docker 명령어**: 서버에 접속해서는 "야, 창고(Docker Hub)에서 최신 버전 가져와서 갈아끼워\!"라고 명령만 내립니다.

이 방식은 **완전히 무료**(e2-micro 사용 시)이며, 특정 클라우드 서비스(Cloud Run 등)에 종속되지 않는 가장 **개발자스러운 배포 방법**입니다.
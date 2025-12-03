# 🚀 Value Pick 배포 가이드 (GCP 통합 배포)

## 🏗 아키텍처: Monolithic Deployment

프론트엔드(React) 빌드 결과물을 백엔드(Spring Boot)의 정적 리소스로 포함시켜, **하나의 JAR 파일로 배포**하는 전략입니다.

- **Infrastructure:** Google Compute Engine (e2-micro, Free Tier)
- **Container:** Docker Compose (Spring Boot + MariaDB + Redis)
- **장점:**
  1.  **비용 0원:** 추가적인 호스팅 서비스(S3, Firebase 등) 비용 없음.
  2.  **보안 이슈 해결:** Same Origin 정책을 따르므로 CORS 및 Mixed Content 문제 원천 차단.
  3.  **관리 단순화:** 서버 한 대, JAR 파일 하나만 관리하면 됨.

---

## 1\. 프로젝트 코드 수정 (Local)

배포 전, 프론트엔드와 백엔드가 서로 '한 몸'이 되도록 코드를 약간 수정해야 합니다.

### A. Frontend 수정 (`FRONT/companyvalue`)

1.  **API 주소 변경 (`src/api/axiosClient.ts`)**

    - 통합 배포 시 브라우저는 현재 접속한 주소(예: `http://34.xx.xx.xx:8080`)를 기준으로 API를 찾습니다. 따라서 절대 경로(`http://...`) 대신 \*\*빈 문자열(상대 경로)\*\*을 사용합니다.

```typescript
// src/api/axiosClient.ts
const axiosClient = axios.create({
  baseURL: "", // 빈 문자열로 설정 (자동으로 현재 도메인/포트 사용)
  headers: {
    "Content-Type": "application/json",
  },
});
```

2.  **빌드 실행**

```bash
npm run build
```

- 결과: `dist` 폴더 생성 확인.

### B. 통합 작업 (File Copy)

프론트엔드 빌드 결과물을 백엔드가 인식할 수 있는 폴더로 옮깁니다.

- **복사 할 곳:** `FRONT/companyvalue/dist/*` (안에 있는 모든 파일 및 폴더)
- **붙여넣을 곳:** `BACK/companyvalue/src/main/resources/static/`
  - _Tip: `static` 폴더가 없다면 새로 생성하세요._
  - _결과 확인: `static/index.html`이 존재해야 합니다._

### C. Backend 수정 (`BACK/companyvalue`)

1.  **CORS 설정 제거 (`SecurityConfig.java`)**

    - 이제 프론트와 백엔드가 같은 출처이므로 CORS 설정이 불필요합니다. 보안을 위해 관련 설정을 제거하거나 주석 처리합니다.

```java
// SecurityConfig.java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        // .cors(...)  <-- 이 부분을 삭제 또는 주석 처리
        ...
}
```

2.  **최종 JAR 파일 빌드**

    - 프론트엔드가 포함된 최종 실행 파일을 만듭니다.

```bash
# BACK/companyvalue 경로에서 실행
./gradlew clean build -x test
```

- 결과: `build/libs/companyvalue-0.0.1-SNAPSHOT.jar` 생성 확인.

---

## 2\. 서버 환경 구축 (Google Compute Engine)

### A. VM 인스턴스 생성

1.  **GCP 콘솔** 접속 -\> Compute Engine -\> VM 인스턴스 만들기.
2.  **설정 (무료 티어 기준):**
    - **리전:** `us-west1` (오리건) 또는 `us-central1` (아이오와).
    - **머신 유형:** `e2-micro` (vCPU 2개, 메모리 1GB).
    - **부팅 디스크:** Ubuntu 22.04 LTS (표준 영구 디스크 30GB).
    - **방화벽:** HTTP/HTTPS 트래픽 허용 체크.

### B. 네트워크 설정

1.  **고정 IP 예약:**
    - VPC 네트워크 \> IP 주소 \> 외부 IP의 점 3개 클릭 \> **[고정 IP 주소로 승격]**.
2.  **방화벽 규칙 (8080 포트 개방):**
    - VPC 네트워크 \> 방화벽 \> [규칙 만들기].
    - 대상: `네트워크의 모든 인스턴스`, 소스 IPv4: `0.0.0.0/0`.
    - 프로토콜/포트: `tcp: 8080`.

### C. 서버 기초 세팅 (SSH 접속)

`e2-micro`의 적은 메모리(1GB)를 보완하기 위해 **스왑 메모리 설정이 필수**입니다.

```bash
# 1. 스왑 메모리 2GB 설정 (OOM 방지)
sudo fallocate -l 2G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab

# 2. Docker & Docker Compose 설치
sudo apt update
sudo apt install -y docker.io docker-compose
sudo usermod -aG docker $USER
newgrp docker

# 3. 타임존 설정 (KST)
sudo timedatectl set-timezone Asia/Seoul
```

---

## 3\. 배포 실행 (Deployment)

### A. 프로젝트 설정

```bash
# 1. 프로젝트 클론
git clone https://github.com/alstn9213/company_value_project.git
cd company_value_project

# 2. 환경 변수 파일 생성
nano .env
```

- `.env` 내용 작성:

```env
DB_ROOT_PASSWORD=자신만의_비밀번호
DB_NAME=value
```

### B. JAR 파일 전송 및 배포

로컬에서 빌드한 \*\*'통합 JAR 파일'\*\*을 서버로 업로드합니다. (서버에서 빌드하지 마세요. 멈춥니다.)

1.  **파일 업로드 (GCP SSH 창):**

    - SSH 창 우측 상단 톱니바퀴(`⚙️`) \> **[파일 업로드]** 클릭.
    - 로컬의 `BACK/companyvalue/build/libs/companyvalue-0.0.1-SNAPSHOT.jar` 선택.

2.  **파일 이동 및 실행 (서버 터미널):**

```bash
# 1. Dockerfile이 참조할 위치로 JAR 이동 및 이름 변경
mkdir -p BACK/companyvalue/build/libs/
mv ~/BACK/companyvalue/build/libs/companyvalue-0.0.1-SNAPSHOT.jar ~/company_value_project/BACK/companyvalue/build/libs/app.jar

# 2. Docker Compose 실행 (빌드 옵션 포함)
docker-compose up -d --build
```

3.  **확인:**

    - `docker logs -f company_backend` 명령어로 로그 확인.
    - 브라우저에서 `http://[고정IP]:8080` 접속 시 리액트 화면이 나오면 성공\! 🚀

4.  테스트 컨트롤러 url로 api 호출해서 DB에 데이터 적재

- **회사 정보:** `http://34.69.43.90:8080/test/schedule/run`
- **거시 경제 정보:** `http://34.69.43.90:8080/test/macro/init`

---

## 4\. 운영 및 관리

- **재배포 시:**

  1. 코드 수정 후 cmd 창 오픈. 이후 두가지 상황에 따라 다름

  - 백엔드만 수정했을 경우

    1. `cd BACK/companyvalue`
    2. `./gradlew clean build -x test`

  - 프론트 엔드 코드도 수정했을 경우
    1. `npm run build`
    2. `resources/static` 폴더 안의 파일들 복사
    3. `./gradlew build`.

  2.  구글 클라우드 ssh 브라우저에서 새로운 JAR 파일 업로드.

  3.  서버에서 `mv ~/BACK/companyvalue/build/libs/companyvalue-0.0.1-SNAPSHOT.jar ~/company_value_project/BACK/companyvalue/build/libs/app.jar`로 덮어쓰기 후 `docker-compose up -d --build` 실행.

- **로그 확인:** `docker logs -f company_backend` (실시간 로그)

- **데이터베이스 접속:**

  - 서버 내부: `docker exec -it company_mariadb mariadb -u root -p`

  mv companyvalue-0.0.1-SNAPSHOT.jar ~/company_value_project/BACK/companyvalue/build/libs/app.jar

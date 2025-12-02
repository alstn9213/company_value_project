# 🚀 Value Pick 배포 가이드 (GCP + Firebase)

## 🏗 아키텍처 및 전략

  * **Frontend:** Firebase Hosting (HTTPS 기본 제공, 속도 빠름)
  * **Backend:** Google Compute Engine (e2-micro, Free Tier) + Docker Compose (Spring Boot + MariaDB + Redis)

-----

## Part 1. 백엔드 배포 (Google Compute Engine)

### 1\. VM 인스턴스 생성 및 네트워크 설정

1.  **VM 인스턴스 생성**

      * **Region:** `us-west1` (오리건) 또는 `us-central1` (아이오와) (Free Tier 필수)
      * **Machine Type:** `e2-micro`
      * **Boot Disk:** Ubuntu 22.04 LTS (x86/64, 30GB)
      * **Firewall:** HTTP, HTTPS 트래픽 허용 체크

2.  **고정 IP 예약 (External IP)**

      * GCP 콘솔 \> VPC 네트워크 \> IP 주소 \> 해당 VM의 외부 IP의 점 3개 클릭 \> **[고정 IP 주소로 승격]**
      * *이 IP 주소(`34.xx.xx.xx`)를 기록해두세요. 프론트엔드에서 API 주소로 사용합니다.*

3.  **방화벽 규칙 설정 (8080 포트)**

      * GCP 콘솔 \> VPC 네트워크 \> 방화벽 \> [방화벽 규칙 만들기]
      * 이름: `allow-springboot-8080`(마음대로)
      * 대상: 네트워크의 모든 인스턴스
      * 소스 IPv4 범위: `0.0.0.0/0`
      * 프로토콜 및 포트: `tcp: 8080`

### 2\. 서버 기초 환경 세팅 (SSH 접속)

`e2-micro`는 메모리가 1GB뿐이므로 **Swap 메모리 설정이 필수**입니다. 설정하지 않으면 빌드/배포 중 서버가 멈춥니다.

```bash
# 1. 스왑 메모리 2GB 설정
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

# 3. Timezone 설정 (한국 시간)
sudo timedatectl set-timezone Asia/Seoul
```

### 3\. 프로젝트 코드 및 환경 변수 설정

서버에서 소스코드를 받아오고, 데이터베이스 비밀번호 등이 담긴 `.env` 파일을 생성합니다.

```bash
# 1. 깃허브에서 프로젝트 클론
git clone https://github.com/alstn9213/company_value_project.git
cd company_value_project

# 2. .env 파일 생성 (루트 디렉토리)
nano .env
```

**`.env` 파일 내용 (수정해서 입력 후 Ctrl+X, Y, Enter 저장)**

```env
DB_ROOT_PASSWORD=실제_사용할_복잡한_비밀번호
DB_NAME=value
```

### 4\. 로컬 빌드 및 JAR 파일 전송 (중요 수정 사항)

서버 성능 문제로 인해 **로컬 컴퓨터에서 빌드 후 JAR 파일만 서버로 전송**하는 방식을 사용합니다.
*(기존 문서의 Dockerfile 경로 문제 해결을 위해 전송 위치를 정확히 지정합니다)*

**[내 컴퓨터]에서 실행:**

```bash
# 1. 백엔드 프로젝트 위치로 이동
cd BACK/companyvalue

# 2. JAR 파일 빌드 (테스트 제외하고 빠르게)
./gradlew clean build -x test

# 3. 빌드된 JAR 파일을 서버의 특정 경로로 전송
# GCP 웹 콘솔의 '파일 업로드' 기능을 사용하면 편합니다.
# 업로드 위치는 서버의 /home/계정명/company_value_project/BACK/companyvalue/build/libs/ 여야 합니다.
```

**[GCP 웹 콘솔 파일 업로드 팁]**

1.  SSH 창 우측 상단 `⚙️` \> `파일 업로드` 클릭
2.  로컬의 `BACK/companyvalue/build/libs/companyvalue-0.0.1-SNAPSHOT.jar` 선택
3.  업로드 완료 후 서버 터미널에서 파일을 제 위치로 이동:
```bash
# 업로드된 파일 이동 (Dockerfile이 참조하는 위치로)
mkdir -p BACK/companyvalue/build/libs
mv ~/companyvalue-0.0.1-SNAPSHOT.jar ./BACK/companyvalue/build/libs/app.jar

# 주의: Dockerfile 내용 수정 필요
# 현재 Dockerfile은 build/libs/*.jar를 참조하므로 파일명을 app.jar로 변경하고 
# Dockerfile도 수정하거나, 파일명을 그대로 두고 경로만 맞춥니다.
# 가장 쉬운 방법: Dockerfile 수정 없이 경로만 맞추기
```

**[서버]에서 Dockerfile 대응을 위한 파일 이동 및 이름 변경:**

```bash
# 프로젝트 루트(company_value_project)에서 실행
mkdir -p BACK/companyvalue/build/libs
# 업로드한 파일이 홈(~)에 있다고 가정 시:
mv ~/companyvalue-0.0.1-SNAPSHOT.jar BACK/companyvalue/build/libs/
```

### 5\. Docker Compose 실행

```bash
# 프로젝트 루트에서 실행
docker-compose up -d --build

# 로그 확인 (에러 없는지 체크)
docker logs -f company_backend
```

-----

## Part 2. 프론트엔드 배포 (Firebase Hosting)

### 1\. 파이어베이스 프로젝트 생성

1.  [Firebase Console](https://console.firebase.google.com/) 접속 \> 프로젝트 추가.
2.  프로젝트 이름 입력 (예: `company-value-portfolio`).
3.  Google Analytics는 꺼도 무방합니다.

### 2\. 로컬 프로젝트 설정 (내 컴퓨터)

프론트엔드 코드에 백엔드 주소를 연결하고 빌드합니다.

**1. 환경 변수 설정 (.env.production)**
`FRONT/companyvalue` 폴더 안에 `.env.production` 파일을 생성합니다.

```env
# 아까 만든 GCP 고정 IP 주소 (http:// 포함, 뒤에 /는 뺌)
# 주의: HTTPS가 적용되지 않은 IP이므로 http:// 입니다.
VITE_API_BASE_URL=http://34.12.34.56:8080
```

**2. Axios 클라이언트 수정 (`src/api/axiosClient.ts`)**
코드가 하드코딩 된 `localhost` 대신 환경변수를 바라보게 수정해야 합니다.

```typescript
// src/api/axiosClient.ts 수정
import axios from 'axios';
import { useAuthStore } from '../stores/authStore';

// 환경 변수에서 URL 가져오기 (없으면 로컬호스트)
const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const axiosClient = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type' : 'application/json',
  },
});
// ... (나머지 코드는 동일)
```

**3. 빌드 실행**

```bash
cd FRONT/companyvalue
npm install
npm run build
```

`dist` 폴더가 생성되었는지 확인합니다.

### 3\. Firebase CLI 설정 및 배포

터미널(내 컴퓨터)에서 진행합니다.

```bash
# 1. Firebase Tools 설치 (최초 1회)
npm install -g firebase-tools

# 2. 로그인 (브라우저 창이 열리면 로그인)
firebase login

# 3. 프로젝트 초기화 (FRONT/companyvalue 폴더 안에서 실행)
firebase init
```

**`firebase init` 선택지 가이드:**

1.  **Which features...?**: `Hosting: Configure files for Firebase Hosting...` (스페이스바로 선택 후 엔터)
2.  **Select a default Firebase project**: `Use an existing project` 선택 후 아까 만든 프로젝트 선택.
3.  **What do you want to use as your public directory?**: `dist` 입력 (**중요\!**)
4.  **Configure as a single-page app?**: `Yes` (React는 SPA이므로 필수)
5.  **Set up automatic builds and deploys with GitHub?**: `No` (나중에 설정 가능)
6.  **File dist/index.html already exists. Overwrite?**: `No` (빌드한 파일 유지)

**4. 배포하기**

```bash
firebase deploy
```

배포가 완료되면 `Hosting URL` (예: `https://company-value-project.web.app`)이 나옵니다. 접속해서 확인해 보세요.

-----

## ⚠️ 트러블 슈팅 및 주의사항 (Mixed Content)

**가장 중요한 문제:**
현재 \*\*Frontend는 HTTPS(Firebase)\*\*이고 \*\*Backend는 HTTP(GCP IP)\*\*입니다.
브라우저 보안 정책상 **HTTPS 사이트에서 HTTP API를 호출하면 "Mixed Content" 오류가 발생하며 통신이 차단**됩니다.

**해결 방법 (포트폴리오용):**

1.  **방법 A (간단):** Firebase Hosting을 쓰지 않고, S3 버킷(AWS)이나 GCS 버킷(GCP)을 이용해 **HTTP로 웹사이트를 호스팅**합니다. (보안 경고가 뜨지만 작동은 함)
2.  **방법 B (정석):** GCP 백엔드에 도메인을 연결하고 **Nginx + Let's Encrypt**를 사용하여 HTTPS를 적용합니다.
3.  **방법 C (테스트용):** Chrome 브라우저 설정에서 해당 사이트에 대해 `안전하지 않은 콘텐츠 허용`을 설정하고 시연합니다.

> **추천:** 일단 배포 프로세스 익히는 것이 목적이라면 위 과정을 진행하되, 배포 후 데이터가 안 불러와진다면 F12(개발자 도구) Console에 "Mixed Content" 오류가 있는지 확인하세요. 오류가 있다면 **방법 B**를 적용하거나, 프론트엔드도 GCP 인스턴스 안에 Nginx로 띄워서 HTTP로 통일하는 방법을 고려해야 합니다.


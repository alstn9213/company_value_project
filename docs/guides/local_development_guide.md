# 📄 로컬 개발 환경 실행 가이드 (Local Development Guide)

## 1. 개요 (Overview)

본 프로젝트는 배포 시 **Spring Boot가 React 빌드 파일(Static Resources)을 포함하여 단일 `.jar`로 배포**되는 구조를 가집니다.
따라서 로컬 개발 환경에서는 프론트엔드와 백엔드를 분리하여 실행하되, Vite Proxy를 통해 배포 환경과 동일한 API 요청 구조를 모방하여 실행합니다.

### 📌 아키텍처 변경 사항 반영

  * **Production**: Client → Spring Boot (8080) [Static File + API 통합 처리]
  * **Local Dev**: Client (5173) → **Vite Proxy** → Spring Boot (8080) → DB (Docker)

-----

## 2. 사전 준비 (Prerequisites)

  * **Docker & Docker Compose**: 데이터베이스(MariaDB, Redis) 실행용
  * **Java JDK 17+**: Spring Boot 실행용
  * **Node.js & npm**: React 실행용

-----

## 3. 실행 절차 (Step-by-Step)

### Step 1. 인프라 실행 (Database & Redis)

백엔드 서버(`backend`)를 제외하고, 데이터베이스 컨테이너만 Docker로 실행합니다.

```bash
# 프로젝트 루트 경로에서 실행
docker-compose up -d mariadb redis
```

  * **MariaDB Port**: 로컬 `3310` 포트로 바인딩 (Spring Boot 설정과 일치)
  * **Redis Port**: 로컬 `6379` 포트로 바인딩

### Step 2. 백엔드 실행 (Spring Boot)

Docker 내부가 아닌 로컬 IDE(IntelliJ, Eclipse 등)에서 Spring Boot 애플리케이션을 실행합니다.

  * **Main Class**: `CompanyValueApplication`
  * **Server Port**: `8080`
  * **DB Connection**: `jdbc:mariadb://localhost:3310/value` (application.properties 기본 설정 사용)

> **주의:** `docker-compose up` 명령어로 백엔드까지 띄우면 안 됩니다. (포트 충돌 및 디버깅 불가)

### Step 3. 프론트엔드 설정 및 실행 (Vite Proxy)

API 요청을 백엔드로 우회하기 위해 `vite.config.ts`에 프록시 설정을 추가해야 합니다. (최초 1회 설정)

**1. `FRONT/companyvalue/vite.config.ts` 수정**

```typescript
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      // /api 로 시작하는 요청을 8080 포트(백엔드)로 전달
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      // 인증 관련 요청 전달
      '/auth': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      // (필요 시) 그 외 경로 추가
      // '/test': 'http://localhost:8080', 
    }
  }
})
```

**2. 실행**

```bash
cd FRONT/companyvalue
npm install
npm run dev
```

  * **Frontend URL**: `http://localhost:5173`

-----

## 4. 트러블 슈팅 (Troubleshooting)

### Q1. 로그인 시 404 혹은 CORS 에러가 발생합니다.

  * **원인**: 브라우저가 프론트엔드(`localhost:5173`)가 아닌 백엔드(`localhost:8080`)로 직접 요청을 보냈거나, Proxy 설정이 누락된 경우입니다.
  * **해결**:
    1.  `axiosClient.ts`의 `baseURL`이 비어있는지(`''`) 확인하세요.
    2.  브라우저 접속 주소가 `http://localhost:5173`인지 확인하세요.
    3.  `vite.config.ts`의 `proxy` 설정에 오타가 없는지 확인하고 서버를 재시작하세요.

### Q2. DB 연결 오류가 발생합니다.

  * **원인**: Docker 컨테이너가 실행되지 않았거나 포트가 충돌했습니다.
  * **해결**:
    1.  `docker ps` 명령어로 `company_mariadb` 컨테이너가 `Up` 상태인지 확인하세요.
    2.  로컬의 `3310` 포트를 다른 프로세스가 사용 중인지 확인하세요.

-----

## 5. 배포 시 참고 사항 (Deployment)

  * 배포 빌드 시(`npm run build`) Vite의 `server.proxy` 설정은 자동으로 무시되므로, **코드를 수정하거나 되돌릴 필요 없이** 그대로 빌드하면 됩니다.
  * 생성된 `dist` 폴더의 내용은 Spring Boot의 `src/main/resources/static`으로 복사되어 배포됩니다.
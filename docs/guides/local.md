
### 1. 로컬 실행의 핵심 원칙 (Mental Model)

배포 환경은 "프론트엔드가 통합된 단일 JAR"를 Docker로 실행하지만, **로컬 개발(Local Development) 환경**은 생산성을 위해 여전히 분리해서 실행해야 합니다.

* **Infra:** Docker (DB, Redis만 실행)
* **Backend:** IntelliJ (소스 코드 수정 및 디버깅)
* **Frontend:** Vite (HMR 기능을 통한 빠른 UI 개발)

---

### 2. 단계별 실행 가이드 (표준 절차)

기존 가이드를 준수하되, CI/CD 구축 이후 실수하기 쉬운 **Docker 부분**을 특히 주의해야 합니다.

#### Step 1. 인프라 실행 (가장 중요)

`docker-compose.yml`에 `backend` 서비스가 정의되어 있습니다. 따라서 단순히 `docker-compose up`을 하면 백엔드 컨테이너가 실행되어 **8080 포트를 점유**해버립니다. 이렇게 되면 IntelliJ에서 서버를 띄울 때 포트 충돌 에러가 납니다.

반드시 **DB와 Redis만 콕 집어서** 실행하세요.

```bash
# 프로젝트 루트 경로에서 실행
docker-compose up -d mariadb redis

```

> **멘토의 Tip:** 만약 실수로 `docker-compose up`을 했다면, `docker-compose down`으로 싹 정리하고 다시 위 명령어를 실행하세요.

#### Step 2. 백엔드 실행 (Spring Boot)

IntelliJ에서 실행합니다. 이때 배포용 설정(`prod`)이 아닌 로컬용 설정(`local`)이 적용되는지 확인해야 합니다.

1. **Main Class:** `CompanyValueApplication` 실행
2. **Profile:** `application.properties` 혹은 Run Configuration에서 Active Profile이 `"local"`인지 확인 (보통 `default`가 로컬 설정을 바라보도록 구성합니다).
3. **Secrets:** CI/CD는 GitHub Secrets를 쓰지만, 로컬은 `application-local.properties`를 참조합니다. 이 파일이 로컬에 잘 위치해 있는지 확인하세요.

#### Step 3. 프론트엔드 실행 (Vite Proxy)

프론트엔드는 `vite.config.ts`의 Proxy 설정을 통해 백엔드(localhost:8080)와 통신합니다.

```bash
cd FRONT/companyvalue
npm run dev

```

* 접속 주소: `http://localhost:5173`

---

### 3. CI/CD 연동 후 달라지는 점 & 주의사항

배포 파이프라인이 생겼다고 해서 로컬 코드가 바뀌진 않지만, **환경 설정 관리** 관점에서 다음 두 가지를 신경 써야 합니다.

#### 1) Secrets 관리의 이원화

* **Remote (GitHub Actions):** `.github/workflows/deploy-ssh.yml` 등에서 `${{ secrets... }}`로 주입받습니다.
* **Local:** `application-local.properties` 파일에 직접 적혀 있습니다.
* **주의:** 만약 API Key(Alpha Vantage 등)를 갱신했다면, **GitHub Secrets**와 **로컬 파일** 두 군데 모두 업데이트해야 합니다. 하나만 바꾸고 "왜 안 되지?" 하는 경우가 많습니다.



#### 2) `docker-compose.yml`의 역할 변화

지금 `docker-compose.yml`은 로컬 개발용 DB 실행뿐만 아니라, **서버 배포 시 실제 구동**에도 사용될 가능성이 큽니다.

* 따라서 로컬에서 `docker-compose.yml`의 설정을 수정할 때(예: DB 버전 변경, 볼륨 경로 변경 등)는 **이 변경이 배포 서버에도 영향을 준다는 점**을 인지하고 신중해야 합니다.


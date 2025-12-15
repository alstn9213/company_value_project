# 깃허브 액션으로 자동 배포

전체적인 흐름은 
**GitHub Actions (CI 도구)가 코드를 감지**
-> **React 빌드 & Spring Boot 통합** 
-> **Docker 이미지 생성** 
-> **Google Artifact Registry 저장** 
-> **Cloud Run 배포** 
순서로 진행됩니다.

-----

### 1단계: Google Cloud Platform (GCP) 사전 설정

GCP 콘솔에서 배포를 위한 권한과 저장소를 만들어야 합니다.

1.  **프로젝트 생성 및 API 활성화**:
      * GCP 콘솔에서 `Cloud Run Admin API`, `Artifact Registry API`를 검색해 활성화합니다.
2.  **Artifact Registry 저장소 생성**:
      * Docker 이미지를 저장할 공간입니다.
      * **Artifact Registry** -> **리포지토리 만들기** -> 형식: **Docker**, 리전: `asia-northeast3` (서울)을 추천합니다.
      * 이때 생성된 저장소 주소를 기억해 두세요 (예: `asia-northeast3-docker.pkg.dev/project-id/my-repo`).
3.  **서비스 계정(Service Account) 생성 및 키 발급**:
      * GitHub Actions가 내 GCP에 접근할 수 있도록 열쇠를 만듭니다.
      * **IAM 및 관리** -> **서비스 계정** -> **서비스 계정 만들기**. 
      (만약 ...compute@developer.gserviceaccount.com 처럼 생긴 **"Compute Engine default service account"** 이 있다면 이걸 써도 무방)
      * **권한 부여**: `Cloud Run 관리자`, `서비스 계정 사용자`, `Artifact Registry 작성자` 역할을 부여합니다.
      * **키 생성**: 해당 서비스 계정의 '키' 탭 -> 키 추가 -> 새 키 만들기 -> **JSON** 선택.
      * 다운로드된 JSON 파일의 **전체 내용**을 복사합니다.

### 2단계: GitHub 저장소 Secret 설정

GitHub가 방금 만든 GCP 키를 사용할 수 있도록 등록합니다.

1.  GitHub 리포지토리의 **Settings** -> **Secrets and variables** -> **Actions**로 이동합니다.
2.  **New repository secret**을 클릭합니다.
3.  `GCP_CREDENTIALS`라는 이름으로 아까 복사한 **JSON 키 전체 내용**을 붙여넣습니다.
4.  추가로 `GCP_PROJECT_ID` (내 프로젝트 ID)도 Secret으로 등록하면 관리가 편합니다.
  (Google Cloud Console에 접속하고, 왼쪽 상단 로고 옆에 뜨는 프로젝트를 클릭해서 들어가면 나오는 프로젝트 ID를 복사해서 gitbub actions의 New repository secret에 붙여넣기 하면된다.)
  
### 3단계: GitHub Actions 워크플로우 작성 (`deploy.yml`)

프로젝트 최상단에 `.github/workflows/deploy.yml` 파일을 생성하고 아래 내용을 작성합니다. 이 스크립트는 **프론트엔드를 빌드해서 백엔드 정적 리소스 폴더로 복사한 뒤, 하나의 JAR로 만드는 과정**을 포함합니다.

```yaml
name: Build and Deploy to Cloud Run

on:
  push:
    branches: [ "main" ] # main 브랜치에 push될 때 실행

env:
  PROJECT_ID: ${{ secrets.GCP_PROJECT_ID }} # Secret에 등록한 프로젝트 ID
  GAR_LOCATION: asia-northeast3 # 아까 설정한 리전 (서울)
  REPOSITORY: my-repo # Artifact Registry 리포지토리 이름 (1단계에서 만든 이름)
  IMAGE: company-value-app # 생성할 이미지 이름
  SERVICE: company-value-service # Cloud Run 서비스 이름
  REGION: asia-northeast3

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      # 1. Java 17 세팅 (build.gradle 참고)
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      # 2. Node.js 세팅 (React 빌드용)
      - name: Set up Node.js
        uses: actions/setup-node@v4
        with:
          node-version: '20'

      # 3. 프론트엔드 빌드 (경로 주의: FRONT/companyvalue)
      - name: Install and Build Frontend
        working-directory: ./FRONT/companyvalue
        run: |
          npm ci
          npm run build

      # 4. 빌드된 프론트엔드 리소스를 백엔드로 이동 (통합 배포의 핵심)
      # Vite 빌드 결과물(dist)을 Spring Boot의 static 폴더로 복사합니다.
      - name: Copy Frontend to Backend Static Resources
        run: |
          mkdir -p ./BACK/companyvalue/src/main/resources/static
          cp -r ./FRONT/companyvalue/dist/* ./BACK/companyvalue/src/main/resources/static/

      # 5. Spring Boot 빌드 (테스트 제외 옵션 -x test는 선택사항)
      - name: Build with Gradle
        working-directory: ./BACK/companyvalue
        run: |
          chmod +x gradlew
          ./gradlew clean build -x test

      # 6. GCP 인증
      - name: Google Auth
        id: auth
        uses: 'google-github-actions/auth@v2'
        with:
          credentials_json: '${{ secrets.GCP_CREDENTIALS }}'

      # 7. Docker 인증
      - name: Docker Auth
        id: docker-auth
        uses: 'docker/login-action@v3'
        with:
          registry: ${{ env.GAR_LOCATION }}-docker.pkg.dev
          username: _json_key
          password: ${{ secrets.GCP_CREDENTIALS }}

      # 8. Docker 이미지 빌드 및 Push
      # Dockerfile 위치가 BACK/companyvalue/Dockerfile 이므로 context를 지정해야 함
      - name: Build and Push Container
        run: |
          docker build -t "${{ env.GAR_LOCATION }}-docker.pkg.dev/${{ env.PROJECT_ID }}/${{ env.REPOSITORY }}/${{ env.IMAGE }}:${{ github.sha }}" ./BACK/companyvalue
          docker push "${{ env.GAR_LOCATION }}-docker.pkg.dev/${{ env.PROJECT_ID }}/${{ env.REPOSITORY }}/${{ env.IMAGE }}:${{ github.sha }}"

      # 9. Cloud Run 배포
      - name: Deploy to Cloud Run
        uses: google-github-actions/deploy-cloudrun@v2
        with:
          service: ${{ env.SERVICE }}
          region: ${{ env.REGION }}
          image: ${{ env.GAR_LOCATION }}-docker.pkg.dev/${{ env.PROJECT_ID }}/${{ env.REPOSITORY }}/${{ env.IMAGE }}:${{ github.sha }}
          flags: '--allow-unauthenticated' # 외부 접속 허용 (포트폴리오용)
```

### 4단계: Dockerfile 점검

`BACK/companyvalue/Dockerfile`은 `jar` 파일을 복사해서 실행하는 구조이므로, 위 GitHub Actions의 5번 단계에서 `build`가 성공적으로 끝나면 `build/libs` 폴더에 JAR가 생성되고, Dockerfile이 이를 잘 가져갈 것입니다.

**수정할 점:**
현재 Dockerfile의 `ARG JAR_FILE=build/libs/*.jar` 부분은 빌드 컨텍스트에 따라 경로가 맞지 않을 수 있습니다. GitHub Actions에서 `docker build` 명령을 `./BACK/companyvalue` 폴더를 기준으로 실행하므로(위 스크립트 8번 과정), 현재 Dockerfile은 수정 없이 그대로 사용 가능합니다.

### 5단계: 주의사항 및 팁 (포트폴리오용)

1.  **React 라우팅 처리**: 통합 배포 시, React에서 사용하는 라우터(`react-router-dom`)가 새로고침 시 404 에러를 낼 수 있습니다. Spring Boot의 `WebController`나 설정에서 `/error` 또는 존재하지 않는 경로는 `index.html`로 포워딩해주는 처리가 필요할 수 있습니다.
2.  **포트 설정**: Cloud Run은 기본적으로 `8080` 포트를 사용합니다. Spring Boot의 `application.yml`이나 `env` 설정에서 포트를 변경하지 않았다면(기본 8080) 문제없이 작동합니다.
3.  **환경 변수 관리**: DB 접속 정보(MariaDB, Redis) 등 민감한 정보는 `application.properties`에 직접 적지 마시고, Cloud Run 배포 설정(콘솔 화면)의 **"변수 및 보안 비밀"** 탭에서 환경 변수(`SPRING_DATASOURCE_URL`, `REDIS_HOST` 등)로 주입해 주어야 합니다.

이 설정대로 진행하시면 `git push` 한 번으로 프론트/백엔드가 통합된 최신 버전이 GCP 서버에 자동으로 배포됩니다.
# 구글 클라우드로 배포

## 아키텍처: Hybrid (Firebase + Compute Engine)

비용 효율성과 관리 편의성을 위해 프론트엔드와 백엔드를 분리하는 전략입니다.

1.  **Frontend (React):** **Firebase Hosting**
    * **이유:** 구글이 제공하는 정적 웹 호스팅 서비스입니다. HTTPS(보안)가 무료로 자동 적용되며, 속도가 빠르고 설정이 매우 쉽습니다.
2.  **Backend (Spring Boot) + DB + Redis:** **Google Compute Engine (GCE)**
    * **이유:** `e2-micro` 인스턴스를 특정 리전(us-west1 등)에서 사용하면 **"Always Free"** 등급으로 평생 무료 사용이 가능합니다.
    * *DB(Cloud SQL)와 Redis(Memorystore) 관리형 서비스를 따로 쓰면 비용이 발생하므로, GCE 서버 한 대 안에 도커로 다 띄우는 전략(All-in-One) 사용.*

---

## 백엔드 & DB 서버 구축 (Google Compute Engine)

가장 무거운 작업인 서버 세팅부터 진행합니다.

1.  **VM 인스턴스 생성:**
    * GCP 콘솔 -> Compute Engine -> VM 인스턴스 만들기.
    * **리전:** `us-west1(오리건)` 또는 `us-central1(아이오와)` (무료 티어 필수 조건).

    * **머신 유형:** `e2-micro` (vCPU 2개, 메모리 1GB).

    * **부팅 디스크:** `Ubuntu 20.04 LTS` 또는 `22.04 LTS` (표준 영구 디스크 30GB까지 무료).

    * **방화벽:** HTTP/HTTPS 트래픽 허용 체크.

2.  **서버 IP 고정하기:** 서버를 껐다 켜도 주소가 바뀌지 않게 만듭니다. (지금은 임시 주소라 바뀌면 프론트엔드가 백엔드를 못 찾습니다.)

3.  **방화벽 열기:** 외부(프론트엔드)에서 백엔드(8080포트)로 들어올 수 있게 문을 열어줍니다.

---
### 고정 IP 만들기 (서버 주소 박제)

서버의 IP 주소를 평생 변하지 않게 고정합니다. (VM이 켜져 있으면 무료입니다.)

1.  구글 클라우드 콘솔 왼쪽 메뉴(햄버거 버튼 ☰)를 누릅니다.

2.  **[VPC 네트워크]** -\> **[IP 주소]** 로 들어갑니다.

3.  방금 만든 VM 인스턴스의 \*\*'외부 IP 주소'\*\*가 보일 겁니다. (기본적으로 유형이 '임시'로 설정됨.)

4.  오른쪽 끝의 **점 3개(⋮)** 버튼 클릭 -\> **[고정 IP 주소로 승격]** 클릭.

5.  이름은 아무거나(예: `company-backend-ip`) 입력하고 **[예약]** 클릭.

6.  잠시 후 유형이 \*\*'고정'\*\*으로 바뀌면 성공
    * 이때 나오는 IP 주소 복사. (예: `34.123.45.67`)

-----

### 방화벽 열기 (8080 포트 개방)

스프링 부트가 사용하는 8080번 포트를 열어줘야 프론트엔드가 접속할 수 있습니다.

1.  왼쪽 메뉴에서 **[VPC 네트워크]** -\> **[방화벽]** 으로 이동.

2.  상단에 **[방화벽 규칙 만들기]** 클릭.

3.  다음 내용만 입력하고 나머지는 그대로 두세요.

      * **이름:** `allow-springboot-8080` (원하는 대로)
      * **대상:** `네트워크의 모든 인스턴스`
      * **소스 IPv4 범위:** `0.0.0.0/0` (전 세계 어디서든 접속 허용)
      * **프로토콜 및 포트:** `지정된 프로토콜 및 포트` 체크 -\> `tcp` 체크 -\> 옆칸에 `8080` 입력.

4.  **[만들기]** 클릭.

-----

###  스왑(Swap) 메모리 설정

지금 만든 **`e2-micro` 인스턴스는 램이 1GB**밖에 안 됩니다.
그런데 현재 프로젝트 스택(Spring Boot + MariaDB + Redis)을 돌리려면 최소 2GB 이상의 메모리가 필요합니다. 스왑 설정 없이 서버를 실행하면, **Spring Boot가 켜지다가 OOM Killed (Out Of Memory) 에러를 뱉으며 강제로 종료**됩니다.

1.  구글 클라우드 콘솔의 VM 인스턴스 목록에서 **[SSH]** 버튼을 눌러 터미널 창을 엽니다.

2.  아래 명령어를 실행하세요.


```bash
# 1. 2GB짜리 스왑 파일 생성 (하드디스크 2GB를 램처럼 씀)
sudo fallocate -l 2G /swapfile

# 2. 파일 권한 설정 (루트 계정만 읽고 쓸 수 있게)
sudo chmod 600 /swapfile

# 3. 스왑 영역으로 변환
sudo mkswap /swapfile

# 4. 스왑 메모리 활성화
sudo swapon /swapfile

# 5. 재부팅해도 설정 유지되도록 파일에 등록
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab

# 6. 마지막으로 잘 적용됐는지 확인
free -h
```

결과 화면의 `Swap:` 줄에 `Total`이 `2.0Gi`로 잡혀있으면 성공입니다\!

---

### Jar 파일을 클라우드 서버에 옮기기

프로젝트를 실행하려면
1. 실행 도구(Docker)를 깔고
2. 소스 코드(Git Clone)를 가져와야 합니다.

**⚠️ 중요한 점 (빌드 문제)**:
현재 `e2-micro` 서버(램 1GB)는 성능이 낮아서, 서버에서 직접 자바 코드를 빌드(`gradlew build`)하면 **스왑 메모리가 있어도 시간이 매우 오래 걸리거나 멈출 수 있습니다.**
그래서 "로컬에서 빌드하고 결과물(JAR)만 서버로 보내는 것"을 목표로 합니다.

우선 vm 인스턴스에서 SSH를 클릭해 터미널(SSH)을 엽니다.

```bash
# 1. 패키지 목록 업데이트
sudo apt update

# 2. 도커와 도커 컴포즈 설치
sudo apt install -y docker.io docker-compose

# 3. 도커 권한 부여 (sudo 없이 docker 명령어 쓰기 위함)
sudo usermod -aG docker $USER

# 4. 권한 적용을 위해 로그아웃 후 재접속 (또는 아래 명령어로 세션 갱신)
newgrp docker

# 5. 프로젝트 복사 (본인 레포지토리 주소 입력)
git clone https://github.com/alstn9213/company_value_project.git

# 로컬(내 컴퓨터) 빌드 수행.
# 먼저 배포할 '새로운 JAR 파일'을 만든다.
# 6. 프로젝트 폴더로 이동
cd company_value_project/BACK/companyvalue

./gradlew clean build -x test

```

3.  성공하면 `build/libs` 폴더 안에 `companyvalue-0.0.1-SNAPSHOT.jar` 파일이 생겼을 것입니다.

이제 파일 서버로 전송해야하는데 가장 쉬운 GCP 웹 콘솔을 이용합니다. 이 방법으로 키 파일 설정 없이 브라우저에서 바로 올립니다.

    1.  구글 클라우드 콘솔 -> VM 인스턴스 -> [SSH] 버튼 클릭 (검은 창 열림).

    2.  SSH 창 오른쪽 상단의 [톱니바퀴 아이콘 ⚙️] 클릭 -\> [파일 업로드] 선택.

    3.  내 컴퓨터의 `BACK/companyvalue/build/libs/companyvalue-0.0.1-SNAPSHOT.jar` 파일을 선택.

    4.  업로드가 완료되면 서버의 홈 디렉토리(`~` 또는 `/home/내계정명`)에 파일이 올라갑니다.


파일이 서버에 도착했으니, 이제 **도커 컨테이너에게 새 파일을 보내고 재시작**하면 됩니다.

1.  서버(SSH 창)에서 프로젝트 폴더로 이동합니다. (이미 `git clone` 해둔 상태여야 함)

```bash
cd company_value_project
```

2.  방금 업로드한 JAR 파일을 프로젝트 폴더로 가져오면서 이름을 `app.jar`로 바꿉니다.
    * `docker-compose.yml`이 `app.jar`라는 이름을 찾도록 설정되어 있기 때문입니다.


```bash
# (주의) 계정명은 본인 구글 계정 아이디입니다.
mv /home/계정명/companyvalue-0.0.1-SNAPSHOT.jar ./app.jar
```

3.  **백엔드만** 새로고침합니다. (DB와 Redis는 건드리지 않음)

```bash
docker-compose up -d --build backend
```

* `--build`: 이미지가 있더라도 `app.jar`가 바뀌었으니 다시 빌드하라는 뜻입니다.

4.  잘 떴는지 로그 확인!

```bash
docker logs -f company_backend
```

---

### ⚠️ 미리 대비해야 할 점 (면접 대비 포인트)

1.  **비용 관리 (Budget Alert):**
    * GCP도 "결제 계정" 설정에서 **예산 알림(Budget Alert)**을 설정해두세요. (예: $1 도달 시 알림). 이것만 해두면 요금 폭탄 맞을 일은 없습니다.
2.  **GCE 리전:**
    * Always Free는 미국 리전(`us-west1` 등)에서만 적용됩니다. 한국에서 접속하면 **지연 시간(Latency)**이 조금 발생할 수 있습니다. 면접관이 "왜 느리냐"고 물으면 "비용 효율을 위해 미국 리전의 프리 티어를 사용하여 물리적 거리에 따른 레이턴시가 있습니다"라고 답변하면 완벽합니다.

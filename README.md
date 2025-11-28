<div align="center">

# 📈 기업 가치 평가 & 거시 경제 분석 플랫폼 (Value Pick)

<br/>

기업들의 재무제표와 거시 경제 지표(금리, 실업률 등)를 결합하여 장기 투자하기에 적합한 기업을 분석하는 풀스택 웹 애플리케이션입니다.

<br/>

<img src="https://img.shields.io/badge/Java-17-orange?logo=java&style=flat-square" />
<img src="https://img.shields.io/badge/Spring%20Boot-3.x-green?logo=springboot&style=flat-square" />
<img src="https://img.shields.io/badge/React-18-blue?logo=react&style=flat-square" />
<img src="https://img.shields.io/badge/TypeScript-5.x-3178C6?logo=typescript&style=flat-square" />
<img src="https://img.shields.io/badge/MariaDB-10.6-003545?logo=mariadb&style=flat-square" />
<img src="https://img.shields.io/badge/Redis-Cache-red?logo=redis&style=flat-square" />
<img src="https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&style=flat-square" />

</div>

---

## 📝 프로젝트 개요

단순히 주가를 보여주는 것을 넘어, 기업 재무상태와 시장 상황을 바탕으로 사용자에게 다각화된 투자 인사이트를 제공합니다.
미국 주식 시장 데이터를 기반으로 기업의 **재무 건전성을 100점 만점으로 스코어링**하며, 경기 침체 시그널(장단기 금리차 역전 등) 발생 시 점수를 자동 차감하는 **동적 페널티 시스템**을 구현했습니다.

### 🎯 기획 의도

- **장기투자자에게 적합한 정보 제공:** 수익에 비해 주가가 낮은 기업은 시장에서 저평가 돼있다는 뜻으로 향후 주가가 오를 가능성이 높은 기업을 선별해 보여줍니다.
- **시장 상황 반영:** 재무적으로 탄탄한 기업도 경기 침체기에는 위험할 수 있다는 점에 착안하여 거시 경제 지표를 평가 로직에 반영했습니다.
- **보수적 투자 유도:** 부채가 과도하거나 자본 잠식 상태인 기업을 필터링하여 투자자에게 안전한 선택지를 제안합니다.

---

## 💡 핵심 기능

### 1. 📊 기업 재무 건전성 스코어링

재무제표(손익계산서, 재무상태표, 현금흐름표) 3종을 분석하여 4대 지표를 산출합니다.

- **안정성 (40점):** 부채비율, 영업활동 현금흐름 흑자 여부 평가
- **수익성 (30점):** ROE(자기자본이익률), 영업이익률 분석
- **가치 (20점):** PER, PBR을 활용한 저평가 기업 발굴
- **미래 투자 (10점):** 매출액 대비 R&D 및 설비투자(CapEx) 비중 평가

### 2. ⚠️ 동적 페널티 시스템

시장 상황에 따라 기업의 점수가 동적으로 변동되는 리스크 관리 로직입니다.
| 상황 | 조건 | 페널티 |
| :--- | :--- | :--- |
| **경기 침체 경고** | 장단기 금리차(10Y - 2Y) 역전 발생 시 | **전 기업 -10점** |
| **고금리 위험 투자** | 금리 4% 이상인 상황에서 부채비율 200% 초과 기업이 무리한 투자 감행 시 | **해당 기업 -15점** |
| **투자 부적격(과락)** | 자본 잠식 상태 또는 부채비율 400% 초과 | **즉시 F등급 (0점)** |

### 3. 🌏 거시 경제 대시보드

- **FRED API**를 연동하여 미 10년물/2년물 국채 금리, 인플레이션(CPI), 실업률 데이터를 매일 업데이트합니다.
- **Recharts**를 활용하여 장단기 금리차 역전 구간을 시각적으로 강조(Red Zone)하여 제공합니다.

---

## 🛠 시스템 아키텍처 (System Architecture)

### Tech Stack

- **Backend:** Spring Boot 3, Spring Security (JWT), Spring Data JPA
- **Frontend:** React, TypeScript, Vite, Tailwind CSS, Zustand, TanStack Query
- **Database:** MariaDB (Main), Redis (Caching)
- **Infrastructure:** Docker, Docker Compose
- **External API:** Alpha Vantage (Financials), FRED (Macro Economy)

### Data Pipeline & Optimization

1. **비동기 대용량 처리 (Spring WebClient):**
   - 외부 API의 대용량 JSON 응답을 처리하기 위해 `ExchangeStrategies`로 메모리 버퍼를 **10MB**로 증설하여 `DataBufferLimitException` 해결.
2. **스케줄링 및 캐싱 전략:**
   - **Daily:** 매일 오전 8시 거시 경제 지표 업데이트 (`@Scheduled`).
   - **Weekly:** 매주 일요일 전체 기업 재무 데이터 갱신 및 스코어 재산정.
   - **Redis Cache:** 변동 주기가 긴 거시 경제 데이터와 기업 점수에 `@Cacheable`을 적용하여 조회 성능 최적화 및 API 호출 비용 절감.

---

## 🚀 실행 방법

이 프로젝트는 **Docker Compose**를 통해 DB(MariaDB, Redis)와 백엔드 애플리케이션을 한 번에 실행할 수 있습니다.

### 준비 프로그램

- Docker & Docker Compose installed
- Java 17
- Node.js 18+

### 설치 과정

1. 환경 변수 설정 (`.env`)

   - 프로젝트 루트에 `.env` 파일을 생성하고 데이터베이스 설정을 입력합니다.

   <!-- end list -->

   ```env
   DB_ROOT_PASSWORD=your_password
   DB_NAME=value
   ```

2. Docker 실행 (Backend + DB + Redis)

   ```bash
   # 이미지 빌드 및 컨테이너 실행
   docker-compose up -d --build
   ```

   - 백엔드 서버: `http://localhost:8080`
   - MariaDB: `3310` 포트 매핑
   - Redis: `6379` 포트

3. Frontend 실행 (Local)

   ```bash
   cd FRONT/companyvalue
   npm install
   npm run dev
   ```

   - 프론트엔드 서버: `http://localhost:5173`

---

## 📂 프로젝트 구조

```
├── BACK
│   └── companyvalue
│       ├── controller    # API 엔드포인트 (Auth, Company, Macro 등)
│       ├── domain        # Entity (Company, Score, MacroData)
│       ├── service       # 비즈니스 로직 (Scoring, Scheduling 등)
│       ├── security      # JWT 인증/인가 설정
│       └── config        # Redis, WebClient 설정
├── FRONT
│   └── companyvalue
│       ├── src/pages     # 대시보드, 기업상세, 관심종목 페이지
│       ├── src/components # Recharts 차트 및 공통 UI
│       ├── src/api       # Axios API 클라이언트
│       └── src/stores    # Zustand 전역 상태 관리
└── docker-compose.yml    # 인프라 오케스트레이션
```

---

## 👨‍💻 Developer

**alstn9213**
<br> - GitHub: [https://github.com/alstn9213](https://github.com/alstn9213)
<br> - email: alstn9213@naver.com

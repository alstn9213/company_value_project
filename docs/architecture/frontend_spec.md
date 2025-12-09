# 📘 기업 가치 평가 프로젝트 - 프론트엔드 개발 명세서

## 개요 (Overview)
본 문서는 백엔드 API와 연동하여 클라이언트(React)에서 구현해야 할 5대 핵심 기능의 명세를 정의합니다.
각 기능은 **사용자 경험(UX)** 최적화와 **데이터 시각화**에 중점을 둡니다.

---

## 2. 기술 스택 (Tech Stack)
* **Build Tool**: Vite
* **Language**: TypeScript
* **Framework**: React
* **Styling**: Tailwind CSS
* **State Management**: Zustand (Global), TanStack Query (Server State)
* **Networking**: Axios
* **Visualization**: Recharts

---

## 프로젝트 폴더 구조 (Directory Structure)
도메인 주도 설계(DDD)와 유사하게 기능 단위로 모듈화하여 유지보수성을 높인 구조입니다.

```plaintext
src/
├── api/               # Axios 인스턴스 및 API 호출 함수 (백엔드 Controller와 1:1 매핑)
│   ├── authApi.ts
│   ├── companyApi.ts
│   ├── macroApi.ts
│   └── watchlistApi.ts
├── assets/            # 정적 파일 (이미지, 폰트, 아이콘)
├── components/        # 재사용 가능한 UI 컴포넌트
│   ├── common/        # Button, Input, Card, Modal, Badge
│   ├── charts/        # MacroChart, ScoreRadarChart (Recharts 래핑)
│   └── layout/        # Header, Footer, Sidebar
├── hooks/             # Custom Hooks (비즈니스 로직 및 React Query 훅 분리)
│   ├── useAuth.ts
│   └── useCompanyData.ts
├── pages/             # 라우트 단위 페이지 컴포넌트
│   ├── Home/          # 거시경제 대시보드 (메인)
│   ├── CompanyList/   # 기업 목록 및 검색
│   ├── CompanyDetail/ # 기업 상세 (재무제표, 점수 시각화)
│   ├── Watchlist/     # 관심 종목 관리
│   └── Auth/          # 로그인/회원가입
├── stores/            # 전역 상태 관리 (Zustand) - authStore.ts 등
├── types/             # TypeScript 타입 정의 (백엔드 DTO와 일치)
└── utils/             # 유틸리티 함수 (날짜 포맷팅, 숫자 단위 변환 등)
```


---

## 기능별 상세 명세

### ① 인증 및 보안 (Authentication & Security)
사용자의 접근 권한을 관리하고 개인화된 서비스(관심 종목 등)를 제공하기 위한 기반입니다.

* **Target API**
    * `POST /auth/login`: 로그인 (JWT 발급)
    * `POST /auth/signup`: 회원가입

* **구현 체크포인트**
    1. **Token Storage**: 로그인 성공 시 응답받은 `accessToken`을 `localStorage`에 저장합니다.
    2. **Axios Interceptor**:
       * 모든 HTTP 요청의 Header에 `Authorization: Bearer {token}`을 자동으로 삽입하는 로직을 `api/axiosClient.ts`에 구현합니다.
    3. **Auto Logout**:
       * API 응답 코드가 `401 Unauthorized`일 경우, 토큰 만료로 간주하여 스토리지 삭제 후 로그인 페이지로 강제 리다이렉트합니다.
    4. **Route Guard**:
       * `react-router-dom`의 `<ProtectedRoute>` 컴포넌트를 만들어 비로그인 유저의 접근을 차단합니다.

---

### ② 거시 경제 대시보드 (Macro Dashboard)
시장 상황을 한눈에 파악하여 투자의 방향성을 결정하는 메인 화면입니다.

* **Target API**
    * `GET /api/macro/latest`: 최신 지표 (상단 요약용)
    * `GET /api/macro/history`: 최근 30일 데이터 (차트용)

* **시각화 가이드 (Recharts)**
    * **Component**: `<LineChart>`
    * **X축**: 날짜 (`date`)
    * **Y축**: 금리 (%)
    * **Lines**:
        * 🔵 10년물 국채 금리 (`us10y`)
        * 🟢 2년물 국채 금리 (`us2y`)
        * 🔴 인플레이션/CPI (`inflation`)
    * **Highlight (중요)**:
        * 장단기 금리차 역전 구간(10y < 2y)은 **경기 침체 시그널**이므로, 차트 배경에 `ReferenceArea`를 사용하여 붉은색으로 강조 표시합니다.

---

### ③ 기업 목록 및 검색 (Company List)
수많은 기업 데이터를 효율적으로 탐색하는 기능입니다.

* **Target API**
    * `GET /api/companies?page=0&size=20`: 전체 목록 조회 (Paging)
    * `GET /api/companies/search?keyword={keyword}`: 검색

* **구현 체크포인트**
    1. **Infinite Scroll**:
       * `IntersectionObserver` API를 활용하여 스크롤이 바닥에 닿으면 다음 페이지 데이터를 자동으로 불러옵니다 (React Query `useInfiniteQuery` 권장).
    2. **Search Debounce**:
       * 검색어 입력 시마다 API를 호출하지 않고, 입력이 멈춘 뒤 300~500ms 후에 요청을 보내도록 `debounce` 처리를 합니다.
    3. **Card UI**:
       * 각 기업 카드는 `Glassmorphism` 스타일(반투명 블러 효과)을 적용하여 고급스러운 느낌을 유지합니다.

---

### ④ 기업 상세 분석 (Company Detail) ⭐ Core Feature
개별 기업의 재무 건전성을 입체적으로 분석하는 핵심 페이지입니다.

* **Target API**
    * `GET /api/companies/{ticker}`: 기업 정보 + 스코어 + 최신 재무제표 통합 조회

* **시각화 가이드**
    1. **Score Visualization (Radar Chart)**:
       * 백엔드 `ScoreResult` DTO의 4대 지표를 방사형 차트로 표현합니다.
       * **축 구성**: 안정성(Stability), 수익성(Profitability), 가치(Valuation), 미래투자(Investment)
    2. **Grade Badge**:
       * 종합 점수에 따른 등급(S~F)을 색상 뱃지로 표시합니다.
       * *Color Mapping*: S(Purple), A(Blue), B(Green), C(Yellow), D/F(Red)
    3. **Financial Table**:
       * 매출액, 영업이익 등의 큰 숫자는 가독성을 위해 포맷팅합니다.
       * *Format*: `150,000,000` → **150M** (Million), `2,000,000,000` → **2B** (Billion)

---

### ⑤ 관심 종목 관리 (Watchlist)
사용자가 주목하는 기업을 북마크하여 모아보는 기능입니다.

* **Target API**
    * `GET /api/watchlist`: 내 관심 목록 조회
    * `POST /api/watchlist/{ticker}`: 추가
    * `DELETE /api/watchlist/{watchlistId}`: 삭제

* **구현 체크포인트**
    1. **Optimistic Update (낙관적 업데이트)**:
       * '별(Star)' 아이콘 클릭 시, **서버 응답을 기다리지 않고** UI 색상을 즉시 변경합니다.
       * React Query의 `onMutate`를 활용하여 사용자에게 즉각적인 피드백을 제공하고, 요청 실패 시에만 롤백(Rollback)합니다.
    2. **Access Control**:
       * 로그인하지 않은 사용자가 클릭 시, 로그인 모달을 띄우거나 로그인 페이지로 유도합니다.

---

## 공통 유틸리티 정의 (Utilities)
반복되는 로직을 모듈화하여 코드 중복을 방지합니다.

* **`formatDate(dateString)`**: `YYYY-MM-DD` 형태로 변환
* **`formatCurrency(value)`**: 100M, 1B 단위 변환 및 통화 기호($) 부착
* **`getGradeColor(grade)`**: 등급 문자열(S, A...)을 입력받아 Tailwind CSS 색상 클래스 반환

---
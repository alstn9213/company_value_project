# 시스템 설계 및 기술 구현 명세서

## 1. 시스템 아키텍처
본 서비스는 **Spring Boot(Backend)**와 **React(Frontend)**가 결합된 분리형 아키텍처로 설계되었다. 백엔드는 대용량 외부 데이터 처리를 위해 **WebFlux(WebClient)**를 활용하며, 프론트엔드는 사용자 친화적인 데이터 시각화(Dashboard)를 제공한다.

### 1.1. 기술 스택
* **Backend:** Java 17, Spring Boot 3.x, Spring Data JPA, Spring Security
* **Database:** MariaDB
* **Frontend:** React.js, Recharts (데이터 시각화), Axios
* **HTTP Client:** **Spring WebClient** (비동기 통신 및 대용량 JSON 처리)
* **Security:** JWT (Json Web Token) 기반 인증
* **Build Tool:** Gradle

---

## 2. 데이터베이스 설계 (ERD)
주요 엔티티의 역할 및 연관 관계는 다음과 같다.

### 2.1. Member & Authentication
* **Member:** 사용자 계정 정보 (`email`, `password`, `nickname`, `role`).
* **Watchlist:** 사용자가 관심 있게 지켜보는 기업 목록 (Member:Company = N:M 관계 해소).

### 2.2. Company Data
* **Company:** 종목 코드(`ticker`) 및 섹터 정보.
* **FinancialStatement:** 분기/연도별 재무제표 상세 데이터.
    * 안정성(`TotalLiabilities`, `TotalEquity`), 수익성(`NetIncome`), 활동성(`R&D`, `CapEx`) 지표 포함.
* **CompanyScore:** `ScoringService`를 통해 계산된 최종 점수 및 등급(`grade`), 세부 항목 점수 저장.

### 2.3. Macro Data
* **MacroEconomicData:** `recordedDate`를 PK로 하는 일자별 거시 경제 지표.
    * `fedFundsRate`(기준금리), `us10yTreasuryYield`, `inflationRate` 등 저장.

---

## 3. 핵심 기능 구현 전략

### 3.1. 데이터 수집 및 처리 파이프라인
* **대용량 처리 최적화:** FRED 및 Alpha Vantage의 대형 JSON 응답 처리를 위해 `WebClient`의 **In-Memory Buffer Size를 10MB로 증설** (`ExchangeStrategies` 적용).
* **자동화된 스케줄링:**
    1. **Macro Data (매일 08:00):** 시장 금리 및 경제 지표 최신화.
    2. **Financial & Scoring (매주 일요일 02:00):** 전 종목 재무 데이터 갱신 및 등급 재산정 (API Rate Limit 고려).

### 3.2. 보안 및 인증 (Security & JWT)
* **Stateless Architecture:** 세션을 사용하지 않고 JWT 토큰 방식을 채택하여 확장성 확보.
* **Flow:** `JwtTokenProvider`를 통해 Access Token 발급 및 `JwtAuthenticationFilter`에서 검증 수행. 비밀번호는 `BCrypt`로 암호화 저장.

### 3.3. 스코어링 엔진 (ScoringService)
* **입체적 평가:** 재무제표 기반 정량 평가(안정성/수익성/가치/투자)에 더해, `MacroRepository`의 최신 경제 지표를 반영한 동적 페널티(-10~-15점) 적용.
* **과락 제도:** 자본잠식이나 업종별 기준(일반 400%, 금융 1500%) 초과 시 F등급 부여.

### 3.4. 프론트엔드 시각화 (React Integration)
* **Dashboard:** 거시 경제 지표의 흐름을 라인 차트 등으로 시각화하여 시장 상황을 직관적으로 전달.
* **Score Card:** 기업의 최종 등급(S~F)과 부문별 점수(레이더 차트 등)를 UI 컴포넌트로 구현.

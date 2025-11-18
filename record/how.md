# 시스템 설계 및 기술 구현 명세서

## 1. 시스템 아키텍처
본 서비스는 RESTful API 기반의 **Spring Boot(Backend)**와 **React(Frontend)** 분리형 구조로 설계되었다.
외부 금융 데이터의 대용량 처리와 안정적인 수집을 위해 **Spring WebFlux(WebClient)**를 도입하였다.

### 1.1. 기술 스택
* **Language:** Java 17
* **Framework:** Spring Boot 3.x, Spring Data JPA, Spring Security
* **Database:** MariaDB
* **HTTP Client:** **Spring WebClient** (비동기/논블로킹 처리 및 대용량 버퍼 지원)
* **Build Tool:** Gradle

---

## 2. 데이터베이스 설계 (ERD)
핵심 엔티티 간의 관계 및 주요 필드 정의는 다음과 같다.

### 2.1. Member (회원)
* 사용자 인증 및 관심 목록 관리를 위한 기본 엔티티.
* `email`(ID), `password`(Encrypted), `nickname`

### 2.2. Company (기업)
* 종목 코드와 기본 정보를 관리.
* `ticker`(종목코드), `name`, `sector`, `exchange`(거래소)

### 2.3. FinancialStatement (재무제표)
* 기업의 분기별/연도별 재무 상세 데이터 저장.
* **안정성 지표:** `totalAssets`, `totalLiabilities`, `totalEquity`, `operatingCashFlow`
* **수익성 지표:** `revenue`, `operatingProfit`, `netIncome`
* **투자 지표 (신규):**
    * `researchAndDevelopment`: 연구개발비 (손익계산서)
    * `capitalExpenditure`: 설비투자비 (현금흐름표)

### 2.4. MacroEconomicData (거시 경제)
* 시장 환경 대시보드 및 페널티 로직을 위한 경제 지표 저장.
* `recordedDate`: 기준 일자
* **금리 지표:** `fedFundsRate`(기준금리), `us10yTreasuryYield`(10년물), `us2yTreasuryYield`(2년물)
* **경기 지표:** `inflationRate`(CPI), `unemploymentRate`(실업률)

### 2.5. CompanyScore (기업 점수)
* 계산된 최종 점수 및 세부 항목 점수 저장 (ScoringService 결과).
* `totalScore`, `stabilityScore`, `profitabilityScore`, `valuationScore`
* **`investmentScore`**: 미래 투자 활동에 대한 가산/감점 반영 결과.

---

## 3. 핵심 기능 구현 전략

### 3.1. 외부 API 데이터 수집 파이프라인
* **문제 해결:** FRED API 등에서 대용량 JSON 응답 시 발생하는 `DataBufferLimitException` 해결을 위해 `WebClient`의 메모리 버퍼 사이즈를 10MB로 증설 설정 (`ExchangeStrategies`).
* **수집 주기:** `Spring Scheduler (@Scheduled)`를 활용하여 주기적 자동 실행.
    * **Macro Data:** 매일 1회 (시장 금리 등 변동성 반영).
    * **Financial Data:** 주요 타겟 기업(Mag 7 등) 위주로 야간 배치 작업 수행 (API 호출 제한 고려).

### 3.2. API 통신 및 파싱 (FinancialDataService & MacroDataService)
* **Alpha Vantage:** Income, Balance, Cash Flow가 분리되어 제공되므로, `fiscalDateEnding`을 기준으로 데이터를 병합(Merge)하여 `FinancialStatement` 엔티티로 변환.
* **FRED:** 시계열 데이터 중 가장 최신의 유효한 값(Backtracking logic)을 추출하여 `MacroEconomicData` 엔티티로 저장.

### 3.3. 스코어링 엔진 (ScoringService)
* **트랜잭션 처리:** 재무 데이터 분석과 점수 저장을 하나의 트랜잭션(`@Transactional`)으로 묶어 데이터 무결성 보장.
* **알고리즘 구현:**
    1. **Base Calculation:** 재무제표 데이터를 기반으로 안정성/수익성/가치 점수 산출.
    2. **Bonus:** (R&D + CapEx) / Revenue 비율 분석 후 가산점 부여.
    3. **Penalty:** `MacroRepository`에서 최신 금리 정보를 조회하여 기업의 부채 및 투자 상황과 대조, 위험 페널티 차감.
    4. **Finalize:** 과락 조건 체크 후 최종 등급 및 점수 저장.

---

## 4. 향후 개발 계획
1. **Frontend 연동:** React 기반의 대시보드 UI 구현 및 API 연결.
2. **보안 강화:** JWT 토큰 기반의 인증/인가 시스템 완성.
3. **시각화:** Recharts 라이브러리를 활용한 점수 및 재무 추이 차트 구현.
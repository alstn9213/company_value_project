# 더미 데이터 정합성 확보 및 밸류에이션 채점 로직 고도화

**작성일:** 2025-12-09

## 개요 (Overview)

기존 시스템은 더미 데이터 생성 시 기업 간 편차가 거의 없고, 발행 주식 수가 비현실적으로 적어(100만 주) 주가가 과도하게 높게 형성되는 문제가 있었습니다. 또한, 외부 API 데이터가 없는 더미 기업의 경우 PER/PBR 데이터 부재로 내재 가치 점수가 0점으로 처리되어, 모든 기업의 점수가 획일화되는 현상이 발생했습니다.

이를 해결하기 위해 **데이터 생성 알고리즘에 다양성(Variance)을 부여**하고, **내부 데이터를 활용한 밸류에이션 계산 로직**을 추가했습니다.

## 주요 변경 사항 (Key Changes)

### A. 더미 데이터 생성 로직 (`InitDataConfig`)

| 항목 | 변경 전 (AS-IS) | 변경 후 (TO-BE) | 효과 |
| :--- | :--- | :--- | :--- |
| **발행 주식 수** | 1,000,000주 (암시적) | **100,000,000주 (명시적, 1억 주)** | EPS 감소로 주가가 현실적 범위($10~$200)로 조정됨 |
| **기업 역량** | 섹터별 고정값 + 미세한 랜덤 노이즈 | **`companyQuality` 계수 도입 (0.5 \~ 1.5)** | 우량 기업과 부실 기업의 명확한 실적 차이 발생 |
| **이익 변동성** | 단순 가우시안 분포 | **어닝 쇼크(Earnings Shock) 로직 추가** | 10% 확률로 이익 급감 시나리오 추가하여 리스크 구현 |
| **부채 비율** | 섹터별 고정 비율 | **역량 기반 동적 비율 (`2.0 - Quality`)** | 우량 기업일수록 부채가 적고, 부실 기업은 부채가 많도록 설정 |

### B. 밸류에이션 채점 전략 (`ValuationStrategy`)

| 항목 | 변경 전 (AS-IS) | 변경 후 (TO-BE) | 효과 |
| :--- | :--- | :--- | :--- |
| **의존성** | 외부 API 응답(`overview` JSON)에만 의존 | **내부 데이터(`currentPrice`, `FinancialStatement`) 활용 로직 추가** | 외부 데이터가 없는 더미 기업도 점수 산출 가능 |
| **계산 방식** | API의 "PERatio" 필드 파싱 | API 데이터 부재 시, **직접 계산 (주가 / EPS, 주가 / BPS)** | 데이터 누락 시 `0점` 처리되던 문제 해결 |
| **메서드** | `calculate(fs, overview)` 단일 | **`calculate(fs, overview, currentPrice)` 오버로딩** | 점수 계산 시점의 정확한 주가 반영 가능 |

### C. 점수 산정 서비스 및 데이터 접근 (`ScoringService`, `Repository`)

  * **Repository 기능 확장 (`StockPriceHistoryRepository`):**
      * 기존: 차트용 오름차순(Asc) 조회만 존재.
      * **추가:** 점수 계산용 최신 주가 1건 조회 메서드 (`findTopBy...Desc`) 추가.
  * **서비스 로직 개선 (`ScoringService`):**
      * DB에 저장된 가장 최신의 주가(`StockPriceHistory`)를 조회하여 `ValuationStrategy`에 주입.
      * 차트 데이터(화면)와 분석 리포트(로직) 간의 **주가 데이터 정합성(Consistency) 확보**.

## 코드 상세 비교

### 1) InitDataConfig.java (데이터 생성)

```java
// 핵심 변경 코드 스니펫
double companyQuality = 0.5 + random.nextDouble(); // 기업마다 고유 등급 부여

// 이익률에 Quality 반영 및 어닝 쇼크 확률 도입
double adjustedMargin = profile.operatingMargin * companyQuality;
if (random.nextDouble() < 0.1) {
    operatingProfit = operatingProfit.multiply(BigDecimal.valueOf(-0.5)); // 어닝 쇼크
}

// 주식 수 현실화 (1억 주)
long totalShares = 100_000_000L; 
double targetPrice = (netIncome * 4 / totalShares) * profile.peRatio;
```

### 2\) ValuationStrategy.java (전략 패턴)

```java
// Fallback 로직 추가
} else if (currentPrice != null && currentPrice.compareTo(BigDecimal.ZERO) > 0) {
    // API 데이터가 없으면 DB 데이터로 PER/PBR 직접 역산
    double assumedShares = 100_000_000.0;
    double eps = (fs.getNetIncome().doubleValue() * 4) / assumedShares;
    if (eps > 0) per = currentPrice.doubleValue() / eps;
    // ... PBR 계산 로직 동일
}
```

## 기대 효과 (Impact Analysis)

1.  **현실적인 시뮬레이션:** 모든 기업이 우량하고 주가가 비싼 비현실적인 상황에서 벗어나, 적자 기업, 고평가/저평가 기업 등 다양한 케이스가 생성됩니다.
2.  **분석 리포트의 신뢰도 향상:** 차트에서는 주가가 떨어지는데 점수는 높은 식의 데이터 불일치가 사라지고, 실제 주가를 기반으로 내재 가치를 평가하게 되었습니다.
3.  **다양한 등급 분포:** 기존에 대다수가 'B등급(70점)'에 머물던 현상이 개선되어, 기업의 실적과 주가 괴리율에 따라 **S, A, B, C, D 등급이 고르게 분포**하게 됩니다.


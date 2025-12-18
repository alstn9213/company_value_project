# 리팩토링 기록: 실격(F등급) 폐지 및 페널티 정책 통합

**작성일:** 2025년 12월 9일
**관련 이슈:** Scoring Logic Improvement

## 배경 및 목적

### 기존 문제점
1.  **과도한 실격 처리:** 자본잠식이나 고부채 비율(400% 초과) 발생 시, 다른 지표가 아무리 우수해도 즉시 `F등급(0점)` 처리되어 기업의 잠재력을 평가하지 못함.
2.  **금융 섹터 고려 부족:** 금융업(Financial Services)은 구조적으로 부채 비율이 높음에도 불구하고, 일반 제조업과 동일한 부채 비율 기준(400%)을 적용받아 부당하게 실격 처리됨.
3.  **코드 중복:** `DisqualificationPolicy`와 `PenaltyPolicy`에서 부채 비율을 계산하는 로직이 중복되어 있었음.
4.  **위험한 추천 로직:** 자본 잠식 상태임에도 밸류에이션 점수만 높으면 '저점 매수 기회(Opportunity)'로 분류되는 논리적 오류 존재.

### 리팩토링 목표
1.  **F등급 폐지:** F등급 대신 강력한 **페널티 점수**를 부여하여, 총점에서 차감하는 방식으로 변경 (회생 가능성 열어둠).
2.  **금융업 예외 적용:** 금융 섹터의 부채 비율 허용치를 현실화 (1500%까지 허용).
3.  **구조 단순화:** `DisqualificationPolicy` 인터페이스를 삭제하고 `PenaltyPolicy`로 통합.
4.  **안전장치 강화:** 자본 잠식 기업은 절대 '기회(Opportunity)'로 분류되지 않도록 수정.

---

## 2. 주요 변경 사항 (Key Changes)

### 2.1 정책 변경: 실격 -> 페널티 점수화
`DisqualificationPolicy`를 삭제하고, 해당 로직을 `CompositePenaltyPolicy`로 이동하여 점수 차감 방식으로 변경함.

| 항목 | 기존 (실격) | 변경 (페널티) | 비고 |
| :--- | :--- | :--- | :--- |
| **자본 잠식** | F등급 (0점) | **-40점** | `PENALTY_SCORE_CAPITAL_IMPAIRMENT` |
| **고부채 (일반)** | F등급 (0점) | **-20점** | 400% 초과 시 |
| **고부채 (금융)** | F등급 (0점) | **-20점** | **1500% 초과 시 (기준 완화)** |

### 2.2 코드 구조 개선: 중복 제거 및 유틸리티 메서드 도입
부채 비율 계산 및 임계값 초과 여부를 확인하는 로직을 공통 메서드(`exceedsDebtRatio`)로 추출하여 `PenaltyPolicy` 내 중복을 제거함.

```java
// 리팩토링된 공통 메서드 (CompositePenaltyPolicy.java)
private boolean exceedsDebtRatio(FinancialStatement fs, double generalLimit, double financialLimit) {
    // ... (자본 0 이하 체크 등 안전 로직) ...
    
    // 업종별 임계값 자동 선택
    boolean isFinancial = SECTOR_FINANCIAL.equalsIgnoreCase(fs.getCompany().getSector());
    double threshold = isFinancial ? financialLimit : generalLimit;

    return debtRatio.doubleValue() > threshold;
}
````

### 2.3 `ScoringService` 로직 단순화

`if-else` 분기를 제거하고 선형적인 점수 계산 흐름으로 변경. 가독성을 위해 `static import` 적용.

**Before:**

```java
if (disqualificationPolicy.isDisqualified(fs)) {
    totalScore = 0;
} else {
    // 점수 계산 및 페널티 차감
}
```

**After:**

```java
// 1. 기본 점수 합산
int baseScore = stability + profitability + valuation + investment;

// 2. 페널티 계산 (자본잠식, 고부채 등 포함)
int penalty = penaltyPolicy.calculatePenalty(fs, macro);

// 3. 최종 점수 (0점 미만 방지)
int totalScore = Math.max(0, Math.min(100, baseScore - penalty));
```

### 2.4 '기회(Opportunity)' 선정 로직 수정

페널티가 있어도 가치가 높으면(PBR, PER이 높음) 저점 매수 기회로 판단하되, **자본 잠식 기업은 제외**하는 필수 조건을 추가함.

```java
boolean isOpportunity = (penalty > 0) 
        && (valuation >= OPPORTUNITY_VALUATION_THRESHOLD)
        && (fs.getTotalEquity().compareTo(BigDecimal.ZERO) > 0); // 안전장치 추가
```

-----

## 개선 결과

  * **유연성 확보:** 펀더멘털이 튼튼하지만 일시적인 부채 증가나 외부 요인으로 페널티를 받은 기업도 B\~C 등급으로 평가받을 수 있게 됨.
  * **정합성 향상:** 금융 기업들이 더 이상 부채 비율 때문에 억울하게 F등급을 받지 않음.
  * **안전성 확보:** 자본 잠식 상태의 위험 기업을 추천하는 버그 수정.
  * **가독성 증대:** `ScoringService`의 복잡도가 낮아지고, 상수 처리가 깔끔해짐 (`static import`).

## 추후 과제 (Future Work)

  * 페널티 점수(-20, -40)의 크기가 적절한지 실제 데이터를 돌려보며 튜닝 필요.
  * 금융 섹터 외에 부채 비율이 높을 수 있는 다른 섹터(예: 유틸리티, 항공 등)에 대한 예외 처리 검토.



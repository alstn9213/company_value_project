[Refactoring] 기업 가치 평가 로직 개선 (ScoringService)

1. 배경 (Background)

기존 ScoringService는 기업의 재무제표와 거시 경제 데이터를 기반으로 점수를 계산하는 핵심 비즈니스 로직을 담당하고 있었습니다. 초기 구현 단계에서는 기능 구현에 집중하여 모든 계산 로직과 기준값(임계치)들이 서비스 클래스 내부에 혼재되어 있었습니다.

2. 문제점 (Problem Context)

서비스 코드를 점검하던 중 다음과 같은 유지보수 및 확장성 문제를 발견했습니다.

2.1 매직 넘버(Magic Number)와 하드코딩

문제: 부채비율 기준(400%, 1500%), 가산점 기준(20점, 15점), 금리 임계값(4.0%) 등이 코드 곳곳에 숫자 리터럴로 흩어져 있었습니다.

영향: "금융업의 부채비율 기준을 완화해달라"는 요건이 발생할 경우, 코드를 일일이 찾아다니며 수정해야 하는 위험이 있습니다.

2.2 과도한 책임 (Violation of SRP)

문제: ScoringService가 실행 흐름(Orchestration) 뿐만 아니라 세부 판단 로직(Business Rule) 까지 모두 수행하고 있었습니다.

과락 여부 판단 (isDisqualified)

페널티 계산 (applyMacroPenalty, applyRiskyInvestmentPenalty)

영향: 코드가 길어지고 가독성이 떨어지며, 특정 규칙(예: 과락 로직)만 단위 테스트하기 어렵습니다.

3. 해결 방안 (Solution Strategy)

3.1 Policy Pattern 도입 (책임 분리)

복잡한 비즈니스 판단 로직을 별도의 정책(Policy) 클래스로 위임하여 ScoringService의 무게를 줄였습니다.

DisqualificationPolicy (Interface): 기업 평가 자격 미달(과락) 여부를 판단합니다.

PenaltyPolicy (Interface): 거시 경제 상황과 기업 리스크에 따른 감점 로직을 담당합니다.

ScoringService: 정책 구현체를 주입받아 전체적인 평가 흐름만 관리(Orchestration)합니다.

3.2 상수 클래스 추출 (ScoringConstants)

ScoringConstants: 흩어져 있던 모든 임계값과 점수 기준을 하나의 클래스에서 static final 상수로 관리하도록 변경했습니다.

4. 변경 전/후 비교 (Before & After)


```java

// 변경 전 (Before)

// ScoringService.java 내부
private boolean isDisqualified(FinancialStatement fs) {
    // ... 매직 넘버와 로직이 섞여 있음
    if(debtRatio.doubleValue() > 1500.0) return true; 
    // ...
}


// 변경 후 (After)

// ScoringService.java
// 비즈니스 로직이 정책 객체로 위임되어 코드가 직관적으로 변함
if (disqualificationPolicy.isDisqualified(fs)) {
    totalScore = 0;
    grade = "F";
} else {
    int penalty = penaltyPolicy.calculatePenalty(fs, macro);
    totalScore = totalScore - penalty;
    // ...
}
```

5. 기대 효과 (Benefits)

가독성 향상: 서비스 코드가 "어떻게(How)" 계산하는지가 아니라 "무엇을(What)" 하는지 보여주게 되었습니다.

유지보수성 증대: 기준값 변경 시 ScoringConstants만 수정하면 되며, 로직 변경 시 해당 Policy 클래스만 수정하면 됩니다.

테스트 용이성: 복잡한 재무제표 데이터를 만들지 않고도, Policy 객체를 Mocking하여 서비스 흐름을 쉽게 테스트할 수 있습니다.

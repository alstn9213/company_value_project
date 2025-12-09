# 거시경제 지표 서비스(MacroDataService)

리팩토링 보고서작성일: 2025년 12월 8일
관련 모듈: com.back.domain.macro1.

## 배경 및 목적

프로젝트 배포를 앞두고 코드의 안정성과 유지보수성을 높이기 위해 백엔드 코드 전반을 점검하던 중, 거시경제 지표(FRED API 연동)를 처리하는 MacroDataService에서 다수의 매직 스트링(Magic Strings) 과 하드코딩된 로직을 발견하였습니다.이를 개선하여 타입 안전성(Type Safety) 을 확보하고, 향후 지표 추가 시 변경 범위를 최소화하기 위해 리팩토링을 진행했습니다.

## 기존 문제점 (As-Is)

- 매직 스트링(Magic Strings)의 사용: FRED API 호출에 필요한 Series ID(DGS10, CPIAUCSL 등)와 내부 로직에서 사용하는 식별자(us10y, cpi 등)가 단순 문자열(String)로 하드코딩되어 있었습니다.
  - 오타 위험: 문자열 오타가 있어도 컴파일 타임에 감지되지 않고, 런타임 에러나 데이터 누락으로 이어질 위험이 높음.
  - 가독성 저하: 코드 곳곳에 산재된 문자열 리터럴로 인해 어떤 지표를 처리하는지 한눈에 파악하기 어려움.
  
- 확장성 부족: 새로운 경제 지표를 추가하려면 서비스 코드 내부의 여러 메서드(fetchLatestValue, updateData 등)를 일일이 찾아다니며 수정해야 하는 구조였습니다.

## 개선 방향

1. (To-Be)Enum 도입 (MacroIndicator): 분산된 지표 ID들을 하나의 Enum 클래스로 응집시킵니다.
2. 타입 안전성 확보: 서비스 계층의 메서드 파라미터와 Map의 Key를 String에서 MacroIndicator Enum으로 변경합니다.
3. 로직 단순화: 반복되는 API 호출 및 데이터 매핑 로직을 반복문과 EnumMap을 활용해 간결하게 만듭니다.

### 상세 변경 내역

MacroIndicator Enum 생성지표의 FRED Series ID와 설명을 관리하는 책임을 가진 Enum 상수를 정의했습니다.

```java
@Getter
@RequiredArgsConstructor
public enum MacroIndicator {
    US_10Y("DGS10", "10년물 국채 금리"),
    US_2Y("DGS2", "2년물 국채 금리"),
    FED_FUNDS("DFF", "기준 금리"),
    CPI("CPIAUCSL", "소비자 물가 지수"),
    UNEMPLOYMENT("UNRATE", "실업률");

    private final String seriesId;
    private final String description;
}

// 리팩토링 전 String Key 사용, 하드코딩된 호출
Map<String, Double> data = new HashMap<>();
data.put("us10y", fetchLatestValue("DGS10"));
data.put("cpi", fetchLatestValue("CPIAUCSL"));

// ... 반복 ...

// 변경 후 EnumMap 사용, 반복문을 통한 자동화
Map<MacroIndicator, Double> latestValues = new EnumMap<>(MacroIndicator.class);
for (MacroIndicator indicator : MacroIndicator.values()) {
    latestValues.put(indicator, fetchLatestValue(indicator));
}
```

- 결측치 보정 로직 분리
월간 데이터(CPI, 실업률)가 매일 발표되지 않아 발생하는 null 값을 직전 데이터로 채우는 로직을 별도 메서드(fillMissingMonthlyData)로 분리하여 가독성을 높였습니다.

## 개선 효과
- String 사용으로 오타 위험이 존재했으나 Enum 사용으로 컴파일 시점 검증 가능해짐
- EnumMap<MacroIndicator, Double> 사용으로 메모리/속도 최적화
- 이전에는 지표 추가 시 서비스 로직 수정이 필요했으나 이젠 MacroIndicator에 상수만 추가하면 로직 자동 반영
- 의미를 알 수 없는 문자열(DGS10)이 난무했으나 명시적인 상수(MacroIndicator.US_10Y) 사용으로 가독성 향상


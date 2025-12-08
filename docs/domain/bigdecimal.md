# BigDecimal.ZERO

`BigDecimal.ZERO`는 Java의 `java.math.BigDecimal` 클래스에서 제공하는 '0'이라는 값을 가진 상수(static constant)입니다.

## `BigDecimal.ZERO`를 사용하는 이유

`BigDecimal`은 일반 숫자 타입(`int`, `double` 등)이 아닌 객체(Object)입니다. 따라서 `>`나 `==` 같은 기본 비교 연산자를 사용할 수 없습니다.
대신 `compareTo()` 메소드를 사용하여 값을 비교해야 하는데, 이때 비교 대상으로 '0'을 표현하기 위해 `BigDecimal.ZERO`를 사용합니다.

  - **메모리 효율:** `new BigDecimal(0)`처럼 매번 새로운 객체를 생성하는 것보다, 이미 만들어진 상수(`ZERO`)를 재사용하는 것이 메모리 효율이 좋습니다.

## 코드 내에서의 실제 역할

`StabilityStrategy.java` 파일에서 `BigDecimal.ZERO`는 두 군데에서 '값이 양수인지 확인'하는 용도로 쓰이고 있습니다.

### 부채비율 계산 전 '0' 나누기 방지

```java
// totalEquity(자본총계)가 0보다 큰지 확인
if (totalEquity.compareTo(BigDecimal.ZERO) > 0) {
    // ... 나눗셈 로직 수행
}
```

  - **기능:** 부채비율은 `부채 / 자본`으로 계산하는데, 만약 자본(`totalEquity`)이 **0**이라면 나눗셈 에러(`ArithmeticException: / by zero`)가 발생합니다.
  - **의미:** "자본이 0보다 클 때만 나눗셈을 수행하라"는 안전장치 역할을 합니다.

### 영업활동 현금흐름 흑자 여부 판단

```java
// OperatingCashFlow(영업현금흐름)가 0보다 큰지 확인
if (fs.getOperatingCashFlow().compareTo(BigDecimal.ZERO) > 0)
    score += 20;
```

  - **기능:** 기업이 영업을 통해 실제 현금을 벌어들이고 있는지 확인합니다.
  - **의미:** "영업현금흐름이 양수(+)라면(돈을 벌고 있다면) 점수 20점을 더하라"는 비즈니스 로직입니다.

### 요약

`compareTo(BigDecimal.ZERO) > 0`은 일반적인 숫자 코드로 치면 `if (변수 > 0)`과 똑같은 의미라고 보시면 됩니다.
## 1. BigDecimal.ZERO란?

`BigDecimal`은 자바에서 부동소수점 오차 없이 정확한 숫자를 표현하기 위해 사용하는 **'객체'**입니다. `int`나 `double` 같은 기본형(Primitive Type)이 아니기 때문에 `>`나 `==` 같은 일반 연산자를 사용할 수 없습니다.

그중 `BigDecimal.ZERO`는 `java.math.BigDecimal` 클래스 내부적으로 미리 생성해 둔 **'0' 값을 가진 상수(Static Constant)**입니다.

## 2. 왜 `new BigDecimal(0)` 대신 `ZERO`를 쓸까?

**1) 메모리 효율 (Memory Efficiency)**

* `new BigDecimal(0)`을 사용하면 호출할 때마다 매번 새로운 객체가 Heap 메모리에 생성됩니다.
* 반면, `BigDecimal.ZERO`는 클래스 로딩 시점에 딱 한 번 생성된 **상수 객체를 재사용**하므로 불필요한 객체 생성을 막고 메모리를 절약할 수 있습니다.

**2) 가독성과 편의성**

* 코드를 읽을 때 "이 값은 0이다"라는 의도가 훨씬 명확하게 드러납니다.

## 3. 실제 활용: `compareTo()`와 함께 사용하기

`BigDecimal`은 객체이므로 값의 크기를 비교할 때 `compareTo()` 메서드를 사용해야 합니다.

* **compareTo() 반환값의 의미**
* `1` : 대상이 비교 값보다 큼
* `0` : 두 값이 같음
* `-1` : 대상이 비교 값보다 작음



**실제 코드 예시: 0으로 나누기 방지 (Division by Zero)**
재무 데이터를 다룰 때 부채 비율(`부채 / 자본`) 등을 계산하는 경우가 많습니다. 이때 분모(자본)가 0이면 `ArithmeticException`이 발생하므로, 반드시 사전에 검증해야 합니다.

```java
// 예: 자본총계(totalEquity)가 0보다 큰 경우에만 로직 수행

// Bad: 0과 같은지 비교하기 위해 매번 객체 생성
// if (totalEquity.compareTo(new BigDecimal(0)) > 0)

// Good: 이미 만들어진 상수(ZERO) 활용
if (totalEquity.compareTo(BigDecimal.ZERO) > 0) {
    // 자본이 0보다 큼 (안전하게 나눗셈 가능)
    BigDecimal debtRatio = totalLiabilities.divide(totalEquity, RoundingMode.HALF_UP);
}

```

* **해석:** `compareTo(BigDecimal.ZERO)`가 `1`을 반환하면(`> 0`), `totalEquity`가 0보다 크다는 뜻이므로 안전하게 계산 로직을 수행할 수 있습니다.


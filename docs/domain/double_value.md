# doubleValue()

`doubleValue()`는 객체가 가진 숫자를 `double` (실수) 타입으로 바꾸는 메소드입니다.

`BigDecimal` 같은 객체는 정밀한 계산을 위해 만들어진 **객체**이기 때문에, 일반적인 `+`, `-`, `*`, `/` 연산이나 그래프 라이브러리 등에서 바로 사용할 수 없는 경우가 많습니다.
이때 일반 실수형 숫자(primitive double)로 변환하기 위해 사용합니다.

## 핵심 기능

  * **변환:** `BigDecimal`, `Integer`, `Long` 등의 숫자 객체를 자바의 기본 타입인 `double`로 변환합니다.
  * **용도:**
      * DB나 계산 로직에서는 정밀한 `BigDecimal`을 쓰다가, **최종적으로 화면에 표시하거나(그래프 등), 통계 라이브러리에 값을 넘길 때** 주로 사용합니다.

## `compareTo()`와의 차이점

  * **`compareTo()`**: "내가 쟤보다 큰가?" (비교 목적, **-1/0/1** 반환)
  * **`doubleValue()`**: "나를 실수로 바꿔줘!" (값 변환 목적, **3.14** 같은 실수 반환)

## 사용 예시

```java
BigDecimal bigNum = new BigDecimal("123.45678");

// 1. 비교할 때는 compareTo
if (bigNum.compareTo(BigDecimal.ZERO) > 0) { ... }

// 2. 실제 값을 꺼내서 쓸 때는 doubleValue
double value = bigNum.doubleValue(); // 123.45678 이라는 double 변수가 됨
System.out.println(value + 0.5);     // 123.95678 (이제 일반 계산 가능)
```

## 주의사항 (중요)

`BigDecimal`은 엄청나게 정밀한 숫자도 저장할 수 있지만, `double`은 저장 용량에 한계가 있습니다.
그래서 **`doubleValue()`를 쓰면 미세한 정밀도를 잃어버릴 수 있습니다.**
(예: 돈 계산 같은 아주 중요한 로직 중간에는 쓰지 말고, 마지막에 보여줄 때만 쓰는 것이 좋습니다.)
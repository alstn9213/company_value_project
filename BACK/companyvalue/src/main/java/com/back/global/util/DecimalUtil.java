package com.back.global.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DecimalUtil {


  private DecimalUtil() {}

  // 나눗셈시 반올림 자릿수 유연하게 설정가능
  public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor, int scale) {
    if (dividend == null || divisor == null || isZero(divisor)) {
      return BigDecimal.ZERO;
    }
    return dividend.divide(divisor, scale, RoundingMode.HALF_UP);
  }

  // 나눗셈시 반올림 자릿수 2로 계산
  public static BigDecimal calculatePercentage(BigDecimal dividend, BigDecimal divisor) {
    // 내부적으로는 정밀하게 나누고(6자리), 100을 곱한 뒤, 최종적으로 2자리로 반올림
    BigDecimal ratio = divide(dividend, divisor, 6);
    return ratio.multiply(BigDecimal.valueOf(100))
            .setScale(2, RoundingMode.HALF_UP);
  }

  // 값이 양수인지 확인 (null 이 아니고 0보다 큰 경우)
  public static boolean isPositive(BigDecimal value) {
    return value != null && value.compareTo(BigDecimal.ZERO) > 0;
  }

  // 값이 0인지 확인
  public static boolean isZero(BigDecimal value) {
    return value.compareTo(BigDecimal.ZERO) == 0;
  }

}

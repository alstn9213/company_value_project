package com.back.global.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DecimalUtil {

  private DecimalUtil() {}

  // null 체크하고 나눗셈시 반올림
  public static BigDecimal checkNullAndDivide(BigDecimal dividend, BigDecimal divisor, int scale) {
    if (dividend == null || divisor == null || isZero(divisor)) {
      return BigDecimal.ZERO;
    }
    return dividend.divide(divisor, scale, RoundingMode.HALF_UP);
  }

  // 2째자리에서 반올림한 퍼센트 계산
  public static BigDecimal calculatePercentage(BigDecimal dividend, BigDecimal divisor) {
    // 내부적으로는 정밀하게 나누고(6자리), 100을 곱한 뒤, 최종적으로 2자리로 반올림
    BigDecimal ratio = checkNullAndDivide(dividend, divisor, 6);
    return ratio.multiply(BigDecimal.valueOf(100))
            .setScale(2, RoundingMode.HALF_UP);
  }

  // 값이 0인지 확인하는 헬퍼
  private static boolean isZero(BigDecimal value) {
    return value.compareTo(BigDecimal.ZERO) == 0;
  }

}

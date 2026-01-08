package com.back.global.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DecimalUtil {

  private static final int DEFAULT_CALC_SCALE = 4;

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

  // 0 체크 헬퍼
  private static boolean isZero(BigDecimal value) {
    return value.compareTo(BigDecimal.ZERO) == 0;
  }

}

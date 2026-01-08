package com.back.global.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DecimalUtil {
  private DecimalUtil() {}

  public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor, int scale) {
    if (dividend == null || divisor == null || divisor.compareTo(BigDecimal.ZERO) == 0) {
      return BigDecimal.ZERO;
    }
    return dividend.divide(divisor, scale, RoundingMode.HALF_UP);
  }

}

package com.back.domain.valuation.policy.standard;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Arrays;

public class ProfitabilityStandard {

  @Getter
  @RequiredArgsConstructor
  public enum RoeRule {
    EXCELLENT(20.0, 15),
    GOOD(10.0, 10),
    POOR(0.0, 5);

    private final double threshold;
    private final int score;

    public static int calculate(BigDecimal roe) {
      if (roe == null) return 0;
      double value = roe.doubleValue();

      return Arrays.stream(values())
              .filter(rule -> value >= rule.threshold)
              .findFirst()
              .map(RoeRule::getScore)
              .orElse(0);
    }
  }

  @Getter
  @RequiredArgsConstructor
  public enum OpMarginRule {
    EXCELLENT(20.0, 15),
    GOOD(10.0, 10),
    POOR(0.0, 5);

    private final double threshold;
    private final int score;

    public static int calculate(BigDecimal opMargin) {
      if (opMargin == null) return 0;
      double value = opMargin.doubleValue();

      return Arrays.stream(values())
              .filter(rule -> value >= rule.threshold)
              .findFirst()
              .map(OpMarginRule::getScore)
              .orElse(0);
    }
  }


}


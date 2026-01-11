package com.back.domain.valuation.policy.standard;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public class StabilityStandard {

  public static final int CASH_FLOW_PASS_SCORE = 20;

  @Getter
  @RequiredArgsConstructor
  public enum GeneralDebt {
    EXCELLENT(100.0, 20),
    GOOD(200.0, 10),
    POOR(300.0, 5);

    private final double threshold;
    private final int score;

    public static int calculate(BigDecimal debtRatio) {
      if (debtRatio == null) return 0;
      double value = debtRatio.doubleValue();

      return Arrays.stream(values())
              .filter(rule -> value < rule.threshold)
              .findFirst()
              .map(GeneralDebt::getScore)
              .orElse(0);
    }
  }

  @Getter
  @RequiredArgsConstructor
  public enum FinancialDebt {
    EXCELLENT(800.0, 20),
    GOOD(1000.0, 10),
    POOR(1200.0, 5);

    private final double threshold;
    private final int score;

    public static int calculate(BigDecimal debtRatio) {
      if (debtRatio == null) return 0;
      double value = debtRatio.doubleValue();

      return Arrays.stream(values())
              .filter(rule -> value < rule.threshold)
              .findFirst()
              .map(FinancialDebt::getScore)
              .orElse(0);
    }
  }

}

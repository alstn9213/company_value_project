package com.back.domain.company.service.analysis.policy.standard;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

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

    public static int calculate(double roe) {
      return Arrays.stream(values())
              .filter(standard -> roe >= standard.threshold)
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

    public static int calculate(double opMargin) {
      return Arrays.stream(values())
              .filter(standard -> opMargin >= standard.threshold)
              .findFirst()
              .map(OpMarginRule::getScore)
              .orElse(0);
    }
  }


}


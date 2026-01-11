package com.back.domain.valuation.policy.standard;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Arrays;

public class InvestmentStandard {

  /**
   * 매출액 대비 투자 비중(R&D + CAPEX) 평가 기준
   */
  @Getter
  @RequiredArgsConstructor
  public enum InvestmentRule {
    EXCELLENT(15.0, 10),   // 15% 이상
    GOOD(10.0, 7),  // 10% 이상
    POOR(5.0, 3);      // 5% 이상

    private final double minPercentage;
    private final int score;

    public static int calculate(BigDecimal percentage) {
      if (percentage == null) return 0;
      double value = percentage.doubleValue();

      return Arrays.stream(values())
              .filter(rule -> value >= rule.minPercentage)
              .findFirst() // 순서대로 비교하므로 가장 높은 기준부터 걸러짐 (HIGH -> MEDIUM -> LOW)
              .map(InvestmentRule::getScore)
              .orElse(0);
    }
  }
}
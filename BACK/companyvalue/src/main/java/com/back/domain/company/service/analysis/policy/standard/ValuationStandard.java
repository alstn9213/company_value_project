package com.back.domain.company.service.analysis.policy.standard;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

public class ValuationStandard {

  /**
   * PER (주가수익비율) 평가 기준
   */
  @Getter
  @RequiredArgsConstructor
  public enum PerRule {
    EXCELLENT(0, 15, 10),  // 0 초과 ~ 15 미만
    GOOD(15, 25, 7),         // 15 이상 ~ 25 미만
    POOR(25, 40, 3);   // 25 이상 ~ 40 미만

    private final double min;
    private final double max;
    private final int score;

    public static int calculate(double per) {
      return Arrays.stream(values())
              .filter(rule -> per >= rule.min && per < rule.max)
              .findFirst()
              .map(PerRule::getScore)
              .orElse(0);
    }
  }

  /**
   * PBR (주가순자산비율) 평가 기준
   */
  @Getter
  @RequiredArgsConstructor
  public enum PbrRule {
    EXCELLENT(0, 1.5, 10),
    GOOD(1.5, 3.0, 7),
    POOR(3.0, 5.0, 3);

    private final double min;
    private final double max;
    private final int score;

    public static int calculate(double pbr) {
      return Arrays.stream(values())
              .filter(rule -> pbr >= rule.min && pbr < rule.max)
              .findFirst()
              .map(PbrRule::getScore)
              .orElse(0);
    }
  }
}
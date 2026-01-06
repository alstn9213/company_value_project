package com.back.domain.company.service.analysis.strategy.components;

import com.back.domain.company.service.analysis.constant.ScoreCategory;
import com.back.domain.company.service.analysis.dto.MarketMetrics;
import com.back.domain.company.service.analysis.dto.ScoringData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class ValuationStrategy implements ScoringStrategy {

  @Override
  public int calculate(ScoringData data) {
    MarketMetrics metrics = data.metrics();

    if (isInvalidMetric(metrics.per()) || isInvalidMetric(metrics.pbr())) {
      log.debug("Valuation 데이터 부적합 (적자 또는 데이터 부족): {}", data.fs().getCompany().getName());
      return 0;
    }

    return calculateScore(metrics.per().doubleValue(), metrics.pbr().doubleValue());
  }

  @Override
  public ScoreCategory getCategory() {
    return ScoreCategory.VALUATION;
  }


  // --- 헬퍼 메서드 ---

  // 점수 계산 헬퍼
  private int calculateScore(double per, double pbr) {
    int score = 0;

    // PER 평가
    if (per > 0 && per < 15) score += 10;
    else if (per >= 15 && per < 25) score += 7;
    else if (per >= 25 && per < 40) score += 3;

    // PBR 평가
    if (pbr > 0 && pbr < 1.5) score += 10;
    else if (pbr >= 1.5 && pbr < 3.0) score += 7;
    else if (pbr >= 3.0 && pbr < 5.0) score += 3;

    return score;
  }

  // 유효한 가치 지표인지 판별하는 헬퍼
  private boolean isInvalidMetric(BigDecimal value) {
    return value == null || value.compareTo(BigDecimal.ZERO) <= 0;
  }


}

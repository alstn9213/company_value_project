package com.back.domain.valuation.strategy.components;

import com.back.domain.valuation.constant.ScoreCategory;
import com.back.domain.valuation.model.MarketMetricsDto;
import com.back.domain.valuation.model.ScoringDataDto;
import com.back.domain.valuation.policy.standard.ValuationStandard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class ValuationStrategy implements ScoringStrategy {

  @Override
  public int calculate(ScoringDataDto data) {
    String companyName = data.fs().getCompany().getName();
    MarketMetricsDto metrics = data.metrics();

    return calculateValueScore(companyName, metrics);
  }

  @Override
  public ScoreCategory getCategory() {
    return ScoreCategory.VALUATION;
  }

  // --- 헬퍼 메서드 ---

  // [PER + PBR] 점수 계산 헬퍼
  private int calculateValueScore(String companyName, MarketMetricsDto metrics) {
    BigDecimal per = metrics.per();
    BigDecimal pbr = metrics.pbr();

    if (per == null || pbr == null) {
      log.warn("[데이터 누락] {}: PER={} PBR={}", companyName, per, pbr);
      return 0;
    }

    int pbrScore = ValuationStandard.PbrRule.calculate(pbr);
    int perScore = ValuationStandard.PerRule.calculate(per);

    return pbrScore + perScore;
  }


}

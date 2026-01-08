package com.back.domain.company.service.analysis.strategy.components;

import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.service.analysis.constant.ScoreCategory;
import com.back.domain.company.service.analysis.dto.MarketMetrics;
import com.back.domain.company.service.analysis.dto.ScoringData;
import com.back.domain.company.service.analysis.policy.standard.ValuationStandard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class ValuationStrategy implements ScoringStrategy {

  @Override
  public int calculate(ScoringData data) {
    MarketMetrics metrics = data.metrics();
    BigDecimal pbr = metrics.pbr();
    BigDecimal per = metrics.per();

    if (isInvalidMetric(metrics.per()) || isInvalidMetric(metrics.pbr())) {
      log.debug("Valuation 데이터 부적합 (적자 또는 데이터 부족): {}", data.fs().getCompany().getName());
      return 0;
    }

    return ValuationStandard.PbrRule.calculate(pbr) + ValuationStandard.PerRule.calculate(per);
  }

  @Override
  public ScoreCategory getCategory() {
    return ScoreCategory.VALUATION;
  }


  // --- 헬퍼 메서드 ---

  private int calculatePERScore(MarketMetrics marketMetrics) {
    double rawPer = marketMetrics.per().doubleValue();

    if (rawPer == null) {
      log.warn("per");
    }

    double per = rawPer.doubleValue();
    return ValuationStandard.PbrRule.calculate(per);
  }

  private int calculatePBRSCore(FinancialStatement fs) {
    return ValuationStandard.PerRule.calculate(per);
  }


  // 유효한 가치 지표인지 판별하는 헬퍼
  private boolean isInvalidMetric(BigDecimal value) {
    return value == null || value.compareTo(BigDecimal.ZERO) <= 0;
  }


}

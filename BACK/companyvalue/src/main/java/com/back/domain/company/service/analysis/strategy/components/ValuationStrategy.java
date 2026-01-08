package com.back.domain.company.service.analysis.strategy.components;

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
    String companyName = data.fs().getCompany().getName();
    MarketMetrics metrics = data.metrics();
    BigDecimal pbr = metrics.pbr();
    BigDecimal per = metrics.per();

    if (per == null || pbr == null) {
      log.warn("Valuation 데이터 부족 [Company: {}]: PER={}, PBR={}", companyName, per, pbr);
      return 0;
    }

    if (isNotPositive(per) || isNotPositive(pbr)) {
      log.info("Valuation 평가 제외 (적자 또는 자본잠식) [Company: {}]: PER={}, PBR={}", companyName, per, pbr);
      return 0;
    }

    int pbrScore = ValuationStandard.PbrRule.calculate(pbr);
    int perScore = ValuationStandard.PerRule.calculate(per);

    return pbrScore + perScore;
  }

  @Override
  public ScoreCategory getCategory() {
    return ScoreCategory.VALUATION;
  }


  // --- 헬퍼 메서드 ---

  private boolean isNotPositive(BigDecimal value) {
    return value.compareTo(BigDecimal.ZERO) <= 0;
  }


}

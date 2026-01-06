package com.back.domain.company.service.analysis.strategy.components;

import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.service.analysis.constant.ScoreCategory;
import com.back.domain.company.service.analysis.dto.ScoringData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Component
public class InvestmentStrategy implements ScoringStrategy {

  @Override
  public int calculate(ScoringData data) {
    FinancialStatement fs = data.fs();
    BigDecimal revenue = data.fs().getRevenue();
    BigDecimal rnd = fs.getResearchAndDevelopment();
    BigDecimal capex = fs.getCapitalExpenditure();

    if (revenue == null || rnd == null || capex == null) {
      log.debug("Investment 데이터 누락: {}", fs.getCompany().getName());
      return 0;
    }

    if (revenue.compareTo(BigDecimal.ZERO) == 0) return 0;

    BigDecimal investmentSum = rnd.add(capex);
    double ratio = investmentSum.divide(revenue, 4, RoundingMode.HALF_UP).doubleValue() * 100;

    if (ratio >= 15) return 10;
    else if (ratio >= 10) return 7;
    else if (ratio >= 5) return 3;

    return 0;
  }

  @Override
  public ScoreCategory getCategory() {
    return ScoreCategory.INVESTMENT;
  }


}

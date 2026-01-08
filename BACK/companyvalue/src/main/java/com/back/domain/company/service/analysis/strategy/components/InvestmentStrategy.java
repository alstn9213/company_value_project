package com.back.domain.company.service.analysis.strategy.components;

import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.service.analysis.constant.ScoreCategory;
import com.back.domain.company.service.analysis.dto.ScoringData;
import com.back.domain.company.service.analysis.policy.standard.InvestmentStandard;
import com.back.global.util.DecimalUtil;
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
    String companyName = fs.getCompany().getName();
    BigDecimal revenue = data.fs().getRevenue();
    BigDecimal rnd = fs.getResearchAndDevelopment();
    BigDecimal capex = fs.getCapitalExpenditure();

    if (revenue == null || rnd == null || capex == null) {
      log.warn("Investment 데이터 누락 [Company: {}]: Revenue={}, R&D={}, CAPEX={}",
              companyName, revenue, rnd, capex);
      return 0;
    }

    if (!DecimalUtil.isPositive(revenue)) {
      log.info("Investment 평가 제외 (매출액 0 이하) [Company: {}]", companyName);
      return 0;
    }

    BigDecimal totalInvestment = rnd.add(capex);
    BigDecimal investmentRatio = DecimalUtil.calculatePercentage(totalInvestment, revenue);

    return InvestmentStandard.InvestmentRule.calculate(investmentRatio);
  }

  @Override
  public ScoreCategory getCategory() {
    return ScoreCategory.INVESTMENT;
  }


}

package com.back.domain.company.service.analysis.strategy.components;

import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.service.analysis.constant.ScoreCategory;
import com.back.domain.company.service.analysis.dto.ScoringDataDto;
import com.back.domain.company.service.analysis.policy.standard.InvestmentStandard;
import com.back.global.util.DecimalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class InvestmentStrategy implements ScoringStrategy {

  @Override
  public int calculate(ScoringDataDto data) {
    String companyName = data.fs().getCompany().getName();

    return calculateInvestmentScore(companyName, data);
  }

  @Override
  public ScoreCategory getCategory() {
    return ScoreCategory.INVESTMENT;
  }

  // --- 헬퍼 메서드 ---

  // [투자] 점수 계산 헬퍼
  private int calculateInvestmentScore(String companyName, ScoringDataDto data) {
    FinancialStatement fs = data.fs();
    BigDecimal revenue = data.fs().getRevenue();
    BigDecimal rnd = fs.getResearchAndDevelopment();
    BigDecimal capex = fs.getCapitalExpenditure();

    if (revenue == null || rnd == null || capex == null) {
      log.warn("[데이터 누락] {}: 매출={}, R&D={}, CAPEX={}", companyName, revenue, rnd, capex);
      return 0;
    }

    BigDecimal totalInvestment = rnd.add(capex);
    // 투자 비율 = (투자액 / 매출액) * 100
    BigDecimal investmentRatio = DecimalUtil.calculatePercentage(totalInvestment, revenue);

    return InvestmentStandard.InvestmentRule.calculate(investmentRatio);
  }


}

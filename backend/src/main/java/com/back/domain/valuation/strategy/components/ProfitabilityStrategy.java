package com.back.domain.valuation.strategy.components;

import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.valuation.constant.ScoreCategory;
import com.back.domain.valuation.model.ScoringDataDto;
import com.back.domain.valuation.policy.standard.ProfitabilityStandard;
import com.back.global.util.DecimalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class ProfitabilityStrategy implements ScoringStrategy {

  @Override
  public int calculate(ScoringDataDto data) {
    String companyName = data.fs().getCompany().getName();
    FinancialStatement fs = data.fs();

    int roeScore = calculateROEScore(companyName, fs);
    int opMarginScore = calculateOperatingMarginScore(companyName, fs);

    return roeScore + opMarginScore;
  }

  @Override
  public ScoreCategory getCategory() {
    return ScoreCategory.PROFITABILITY;
  }

  // --- 헬퍼 메서드 ---

  // [ROE] 점수 계산 헬퍼
  private int calculateROEScore(String companyName, FinancialStatement fs) {
    BigDecimal netIncome = fs.getNetIncome();
    BigDecimal equity = fs.getTotalEquity();

    if (netIncome == null || equity == null) {
      log.warn("[데이터 누락] {}: 순이익={} 자본={}", companyName, netIncome, equity);
      return 0;
    }

    // ROE = (당기순이익 / 자본총계) * 100
    BigDecimal roe = DecimalUtil.checkNullAndDivide(netIncome, equity, 4);

    return ProfitabilityStandard.RoeRule.calculate(roe);
  }

  // [영업이익률] 점수 계산 헬퍼
  private int calculateOperatingMarginScore(String companyName, FinancialStatement fs) {
    BigDecimal revenue = fs.getRevenue();
    BigDecimal operatingProfit = fs.getOperatingProfit();

    if (revenue == null || operatingProfit == null) {
      log.warn("[데이터 누락] {}: 매출={}, 영업이익={}", companyName, revenue, operatingProfit);
      return 0;
    }

    // 영업이익률 = (영업이익 / 매출액) * 100
    BigDecimal opMargin = DecimalUtil.checkNullAndDivide(operatingProfit, revenue, 4);

   return ProfitabilityStandard.OpMarginRule.calculate(opMargin);
  }


}

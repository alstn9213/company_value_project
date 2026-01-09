package com.back.domain.company.service.analysis.strategy.components;

import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.service.analysis.constant.ScoreCategory;
import com.back.domain.company.service.analysis.dto.ScoringDataDto;
import com.back.domain.company.service.analysis.policy.standard.ProfitabilityStandard;
import com.back.domain.company.service.analysis.validator.FinancialDataValidator;
import com.back.global.util.DecimalUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProfitabilityStrategy implements ScoringStrategy {

  private final FinancialDataValidator validator;

  @Override
  public int calculate(ScoringDataDto data) {
    FinancialStatement fs = data.fs();

    if (!validator.hasRequiredFields(fs,
            FinancialStatement::getNetIncome,
            FinancialStatement::getTotalEquity,
            FinancialStatement::getRevenue,
            FinancialStatement::getOperatingProfit)) {
      return 0; // 데이터 누락 시 0점 처리 (Validator 내부에서 로그 출력됨)
    }

    int roeScore = calculateROEScore(fs);
    int opMarginScore = calculateOperatingMarginScore(fs);

    return roeScore + opMarginScore;
  }

  @Override
  public ScoreCategory getCategory() {
    return ScoreCategory.PROFITABILITY;
  }

  // --- 헬퍼 메서드 ---

  // [ROE] 점수 계산 헬퍼
  private int calculateROEScore(FinancialStatement fs) {
    // ROE = (당기순이익 / 자본총계) * 100
    BigDecimal roe = DecimalUtil.checkNullAndDivide(fs.getNetIncome(), fs.getTotalEquity(), 4);

    return ProfitabilityStandard.RoeRule.calculate(roe);
  }

  // [영업이익률] 점수 계산 헬퍼
  private int calculateOperatingMarginScore(FinancialStatement fs) {
    // 영업이익률 = (영업이익 / 매출액) * 100
    BigDecimal opMargin = DecimalUtil.checkNullAndDivide(fs.getOperatingProfit(), fs.getRevenue(), 4);

   return ProfitabilityStandard.OpMarginRule.calculate(opMargin);
  }


}

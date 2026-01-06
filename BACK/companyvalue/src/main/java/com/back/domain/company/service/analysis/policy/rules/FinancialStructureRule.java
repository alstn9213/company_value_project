package com.back.domain.company.service.analysis.policy.rules;

import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.macro.entity.MacroEconomicData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.back.domain.company.service.analysis.constant.ScoringConstants.*;

@Slf4j
@Component
public class FinancialStructureRule implements PenaltyRule {

  @Override
  public int apply(FinancialStatement fs, MacroEconomicData macro) {
    BigDecimal equity = fs.getTotalEquity();
    BigDecimal liabilities = fs.getTotalLiabilities();
    int penalty = 0;

    // 자본 잠식 체크
    if (equity.compareTo(BigDecimal.ZERO) <= 0) {
      log.debug("페널티 적용: 자본 잠식(기업: {})", fs.getCompany().getName());
    }

    // 부채 비율 체크
    boolean isFinancial = SECTOR_FINANCIAL.equalsIgnoreCase(fs.getCompany().getSector());
    BigDecimal limitRatio = isFinancial
            ? BigDecimal.valueOf(HIGH_DEBT_RATIO_FINANCIAL)
            : BigDecimal.valueOf(HIGH_DEBT_RATIO_GENERAL);

    BigDecimal debtRatio = liabilities.divide(equity, 4, RoundingMode.HALF_UP)
            .multiply(new BigDecimal("100"));

    if (debtRatio.compareTo(limitRatio) > 0) {
      log.debug("페널티 적용: 과도한 부채비율(기업: {}, 비율: {}%)", fs.getCompany().getName(), debtRatio);
      penalty += PENALTY_SCORE_EXCESSIVE_DEBT;
    }
    return penalty;
  }
}

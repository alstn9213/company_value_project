package com.back.domain.company.service.analysis.policy.rules;

import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.macro.entity.MacroEconomicData;
import com.back.global.util.DecimalUtil;
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
    BigDecimal liabilities = fs.getTotalLiabilities();
    BigDecimal equity = fs.getTotalEquity();
    BigDecimal debtRatio = DecimalUtil.divide(liabilities, equity, 4);

    boolean isFinancial = SECTOR_FINANCIAL.equalsIgnoreCase(fs.getCompany().getSector());

    int penalty = 0;

    // 자본 잠식 체크
    if (equity.compareTo(BigDecimal.ZERO) <= 0) {
      log.debug("[페널티] 자본 잠식: {}", fs.getCompany().getName());
      penalty += PENALTY_SCORE_CAPITAL_IMPAIRMENT;
    }

    // 부채 비율 체크
    BigDecimal limitRatio = isFinancial
            ? BigDecimal.valueOf(HIGH_DEBT_RATIO_FINANCIAL)
            : BigDecimal.valueOf(HIGH_DEBT_RATIO_GENERAL);

    if (debtRatio.compareTo(limitRatio) > 0) {
      log.debug("[페널티] 과도한 부채비율: {}", fs.getCompany().getName());
      penalty += PENALTY_SCORE_EXCESSIVE_DEBT;
    }
    return penalty;
  }
}

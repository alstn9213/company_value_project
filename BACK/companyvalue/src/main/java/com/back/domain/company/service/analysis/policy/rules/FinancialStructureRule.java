package com.back.domain.company.service.analysis.policy.rules;

import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.service.analysis.constant.ScoringConstants;
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
    String company = fs.getCompany().getName();
    int penalty = 0;

    BigDecimal liabilities = fs.getTotalLiabilities();
    BigDecimal equity = fs.getTotalEquity();

    // 자본 잠식 페널티 계산
    if (equity.compareTo(BigDecimal.ZERO) <= 0) {
      log.debug("[페널티] 자본 잠식: {}", company);
      penalty += PENALTY_SCORE_CAPITAL_IMPAIRMENT;
    }

    BigDecimal debtRatio = DecimalUtil.checkNullAndDivide(liabilities, equity, 4);

    boolean isFinance = fs.getCompany().isFinancialSector();
    BigDecimal limitRatio = ScoringConstants.getDebtRatioLimit(isFinance);

    // 부채 페널티 계산
    if (debtRatio.compareTo(limitRatio) > 0) {
      log.debug("[페널티] 과도한 부채비율: {}", company);
      penalty += PENALTY_SCORE_EXCESSIVE_DEBT;
    }

    return penalty;
  }
}

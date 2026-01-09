package com.back.domain.company.service.analysis.policy.rules;

import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.service.analysis.constant.ScoringConstants;
import com.back.domain.company.service.analysis.validator.FinancialDataValidator;
import com.back.domain.macro.entity.MacroEconomicData;
import com.back.global.util.DecimalUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.back.domain.company.service.analysis.constant.ScoringConstants.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class FinancialStructureRule implements PenaltyRule {

  private final FinancialDataValidator validator;

  @Override
  public int apply(FinancialStatement fs, MacroEconomicData macro) {

    if (!validator.hasRequiredFields(fs,
            FinancialStatement::getTotalLiabilities,
            FinancialStatement::getTotalEquity)) {
      return 0; // 데이터가 없으면 페널티 계산 불가 (0 반환)
    }

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

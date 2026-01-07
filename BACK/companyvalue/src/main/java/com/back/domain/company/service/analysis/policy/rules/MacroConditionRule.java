package com.back.domain.company.service.analysis.policy.rules;

import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.macro.entity.MacroEconomicData;
import com.back.global.error.ErrorCode;
import com.back.global.error.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.back.domain.company.service.analysis.constant.ScoringConstants.PENALTY_SCORE_MACRO;

@Slf4j
@Component
public class MacroConditionRule implements PenaltyRule {
  @Override
  public int apply(FinancialStatement fs, MacroEconomicData macro) {
    if (macro.getUs10yTreasuryYield() == null || macro.getUs2yTreasuryYield() == null) {
      throw new BusinessException(ErrorCode.MACRO_DATA_NOT_FOUND);
    }

    // 장단기 금리차 역전 체크
    if (macro.getUs10yTreasuryYield() < macro.getUs2yTreasuryYield()) {
      log.debug("페널티 적용: 장단기 금리차 역전");
      return PENALTY_SCORE_MACRO;
    }

    return 0;
  }
}

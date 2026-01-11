package com.back.domain.valuation.policy;

import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.macro.entity.MacroEconomicData;
import com.back.domain.valuation.policy.rules.PenaltyRule;
import com.back.global.error.ErrorCode;
import com.back.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@Primary
@Component
@RequiredArgsConstructor
public class CompositePenaltyPolicy implements PenaltyPolicy {

  private final List<PenaltyRule> rules;

    @Override
    public int calculatePenalty(FinancialStatement fs, MacroEconomicData macro) {
      if (macro == null) {
        throw new BusinessException(ErrorCode.MACRO_DATA_NOT_FOUND);
      }

      return rules.stream()
              .mapToInt(rule -> rule.apply(fs, macro))
              .sum();
    }

}

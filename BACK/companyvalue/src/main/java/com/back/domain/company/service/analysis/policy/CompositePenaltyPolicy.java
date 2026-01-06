package com.back.domain.company.service.analysis.policy;

import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.service.analysis.policy.rules.PenaltyRule;
import com.back.domain.macro.entity.MacroEconomicData;
import com.back.global.error.ErrorCode;
import com.back.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static com.back.domain.company.service.analysis.constant.ScoringConstants.*;

@Slf4j
@Primary
@Component
@RequiredArgsConstructor
public class CompositePenaltyPolicy implements PenaltyPolicy {

  private final List<PenaltyRule> rules;

    @Override
    public int calculatePenalty(FinancialStatement fs, MacroEconomicData macro) {
      if (macro == null) {
        log.warn("[CompositePenaltyPolicy] 거시 경제 데이터 누락");
      }

      return rules.stream()
              .mapToInt(rule -> rule.apply(fs, macro))
              .sum();
    }

}

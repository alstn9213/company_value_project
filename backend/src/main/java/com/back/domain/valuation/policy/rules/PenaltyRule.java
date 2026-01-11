package com.back.domain.valuation.policy.rules;

import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.macro.entity.MacroEconomicData;

public interface PenaltyRule {
  int apply(FinancialStatement fs, MacroEconomicData macro);
}

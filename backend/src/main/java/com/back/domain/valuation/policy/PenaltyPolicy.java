package com.back.domain.valuation.policy;

import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.macro.entity.MacroEconomicData;

public interface PenaltyPolicy {
    int calculatePenalty(FinancialStatement fs, MacroEconomicData macro);
}
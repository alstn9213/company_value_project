package com.back.domain.company.service.analysis.strategy;

import com.back.domain.company.entity.FinancialStatement;
import com.fasterxml.jackson.databind.JsonNode;

public interface ScoringStrategy {
    int calculate(FinancialStatement fs, JsonNode overview);
}

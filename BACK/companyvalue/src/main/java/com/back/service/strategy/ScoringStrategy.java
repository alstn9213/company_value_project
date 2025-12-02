package com.back.service.strategy;

import com.back.domain.FinancialStatement;
import com.fasterxml.jackson.databind.JsonNode;

public interface ScoringStrategy {
    int calculate(FinancialStatement fs, JsonNode overview);
}

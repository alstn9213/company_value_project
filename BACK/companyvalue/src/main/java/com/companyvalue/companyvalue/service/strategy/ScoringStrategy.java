package com.companyvalue.companyvalue.service.strategy;

import com.companyvalue.companyvalue.domain.FinancialStatement;
import com.fasterxml.jackson.databind.JsonNode;

public interface ScoringStrategy {
    int calculate(FinancialStatement fs, JsonNode overview);
}

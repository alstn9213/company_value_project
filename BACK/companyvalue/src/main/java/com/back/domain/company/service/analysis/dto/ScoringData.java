package com.back.domain.company.service.analysis.dto;

import com.back.domain.company.entity.FinancialStatement;
import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;

public record ScoringData(
        FinancialStatement fs,
        JsonNode overview,
        BigDecimal latestStockPrice
) {

}

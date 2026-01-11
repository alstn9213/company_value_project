package com.back.domain.valuation.model;

import com.back.domain.company.entity.FinancialStatement;

import java.math.BigDecimal;

public record ScoringDataDto(
        FinancialStatement fs,
        MarketMetricsDto metrics,
        BigDecimal latestStockPrice
) {

}

package com.back.global.config.init.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CompanySeedDto(
        String ticker,
        String name,
        String sector,
        String exchange,
        Long totalShares,
        List<FinancialSeedDto> financials,
        List<StockSeedDto> stockHistory
) {
}

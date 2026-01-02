package com.back.global.config.init.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FinancialSeedDto(
        int year,
        int quarter,
        BigDecimal revenue,
        BigDecimal operatingProfit,
        BigDecimal netIncome,
        BigDecimal totalAssets,
        BigDecimal totalLiabilities,
        BigDecimal totalEquity,
        BigDecimal operatingCashFlow,
        BigDecimal researchAndDevelopment,
        BigDecimal capitalExpenditure
) {}
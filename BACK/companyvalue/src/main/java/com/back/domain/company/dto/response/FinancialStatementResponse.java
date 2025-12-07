package com.back.domain.company.dto.response;

import com.back.domain.company.entity.FinancialStatement;

import java.math.BigDecimal;

public record FinancialStatementResponse(
        Integer year,
        Integer quarter,
        BigDecimal revenue,
        BigDecimal operatingProfit,
        BigDecimal netIncome,
        BigDecimal totalAssets,
        BigDecimal totalLiabilities,
        BigDecimal totalEquity,
        BigDecimal operatingCashFlow,
        BigDecimal researchAndDevelopment,
        BigDecimal capitalExpenditure
) {
    public static FinancialStatementResponse from(FinancialStatement fs) {
        return new FinancialStatementResponse(
                fs.getYear(),
                fs.getQuarter(),
                fs.getRevenue(),
                fs.getOperatingProfit(),
                fs.getNetIncome(),
                fs.getTotalAssets(),
                fs.getTotalLiabilities(),
                fs.getTotalEquity(),
                fs.getOperatingCashFlow(),
                fs.getResearchAndDevelopment(),
                fs.getCapitalExpenditure()
        );
    }
}

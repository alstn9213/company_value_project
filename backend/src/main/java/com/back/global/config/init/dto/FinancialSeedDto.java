package com.back.global.config.init.dto;

import com.back.domain.company.entity.Company;
import com.back.domain.company.entity.FinancialStatement;
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
) {
  public FinancialStatement toEntity(Company company) {
    return FinancialStatement.builder()
            .company(company)
            .year(this.year())
            .quarter(this.quarter())
            .revenue(this.revenue())
            .operatingProfit(this.operatingProfit())
            .netIncome(this.netIncome())
            .totalAssets(this.totalAssets())
            .totalLiabilities(this.totalLiabilities())
            .totalEquity(this.totalEquity())
            .operatingCashFlow(this.operatingCashFlow())
            .researchAndDevelopment(this.researchAndDevelopment())
            .capitalExpenditure(this.capitalExpenditure())
            .build();
  }
}
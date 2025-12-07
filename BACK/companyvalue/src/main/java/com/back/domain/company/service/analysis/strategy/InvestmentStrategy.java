package com.back.domain.company.service.analysis.strategy;

import com.back.domain.company.entity.FinancialStatement;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class InvestmentStrategy implements ScoringStrategy {
    @Override
    public int calculate(FinancialStatement fs, JsonNode overview) {
        BigDecimal revenue = fs.getRevenue();
        if (revenue.compareTo(BigDecimal.ZERO) == 0) return 0;

        BigDecimal rnd = fs.getResearchAndDevelopment() != null ? fs.getResearchAndDevelopment() : BigDecimal.ZERO;
        BigDecimal capex = fs.getCapitalExpenditure() != null ? fs.getCapitalExpenditure() : BigDecimal.ZERO;
        BigDecimal investmentSum = rnd.add(capex);

        double ratio = investmentSum.divide(revenue, 4, RoundingMode.HALF_UP).doubleValue() * 100;

        if (ratio >= 15) return 10;
        else if (ratio >= 10) return 7;
        else if (ratio >= 5) return 3;

        return 0;
    }
}

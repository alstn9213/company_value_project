package com.back.service.strategy;

import com.back.domain.FinancialStatement;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class ProfitabilityStrategy implements ScoringStrategy {
    @Override
    public int calculate(FinancialStatement fs, JsonNode overview) {
        int score = 0;
        BigDecimal netIncome = fs.getNetIncome();
        BigDecimal equity = fs.getTotalEquity();
        BigDecimal revenue = fs.getRevenue();
        BigDecimal operatingProfit = fs.getOperatingProfit();

        // 1. ROE (15점)
        if (equity.compareTo(BigDecimal.ZERO) > 0) {
            double roe = netIncome.divide(equity, 4, RoundingMode.HALF_UP).doubleValue() * 100;
            if (roe >= 20) score += 15;
            else if (roe >= 10) score += 10;
            else if (roe >= 0) score += 5;
        }

        // 2. 영업이익률 (15점)
        if (revenue.compareTo(BigDecimal.ZERO) > 0) {
            double opMargin = operatingProfit.divide(revenue, 4, RoundingMode.HALF_UP).doubleValue() * 100;
            if (opMargin >= 20) score += 15;
            else if (opMargin >= 10) score += 10;
            else if (opMargin >= 0) score += 5;
        }
        return score;
    }
}

package com.back.domain.company.service.analysis.strategy;

import com.back.domain.company.entity.FinancialStatement;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class StabilityStrategy implements ScoringStrategy {

    private static final String SECTOR_FINANCIAL = "Financial Services";

    @Override
    public int calculate(FinancialStatement fs, JsonNode overview) {
        BigDecimal totalLiabilities = fs.getTotalLiabilities();
        BigDecimal totalEquity = fs.getTotalEquity();
        int score = 0;
        // 1. 부채비율 평가
        if (totalEquity.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal debtRatio = totalLiabilities.divide(totalEquity, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));

            boolean isFinance = SECTOR_FINANCIAL.equalsIgnoreCase(fs.getCompany().getSector());

            if (isFinance) {
                if (debtRatio.compareTo(BigDecimal.valueOf(800)) < 0) score += 20;
                else if (debtRatio.compareTo(BigDecimal.valueOf(1000)) < 0) score += 10;
                else if (debtRatio.compareTo(BigDecimal.valueOf(1200)) < 0) score += 5;
            } else {
                // 일반 기업 기준
                if (debtRatio.compareTo(BigDecimal.valueOf(100)) < 0) score += 20;
                else if (debtRatio.compareTo(BigDecimal.valueOf(200)) < 0) score += 10;
                else if (debtRatio.compareTo(BigDecimal.valueOf(300)) < 0) score += 5;
            }
        }

        // 2. 영업활동 현금흐름 (흑자 여부)
        if (fs.getOperatingCashFlow().compareTo(BigDecimal.ZERO) > 0) score += 20;

        return score;
    }
}

package com.back.domain.company.service.analysis.strategy;

import com.back.domain.company.entity.FinancialStatement;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.core.util.Json;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ValuationStrategy implements ScoringStrategy {

    @Override
    public int calculate(FinancialStatement fs, JsonNode overview) {
        return calculate(fs, overview, BigDecimal.ZERO);
    }

    public int calculate(FinancialStatement fs, JsonNode overview, BigDecimal latestStockPrice) {
        int score = 0;
        double per = 0.0;
        double pbr = 0.0;

        // 외부 API의 pbr, per
        if(overview != null && overview.has("PERatio")) {
            per = parseDouble(overview, "PERatio");
            pbr = parseDouble(overview, "PriceToBookRatio");

        // 더미 데이터의 pbr, per 계산
        } else if (latestStockPrice != null && latestStockPrice.compareTo(BigDecimal.ZERO) > 0) {
            double assumedShares = 100_000_000.0; // 더미 발행 주식 수
            double annualNetIncome = fs.getNetIncome().doubleValue() * 4;
            double eps = annualNetIncome / assumedShares;

            if (eps > 0) per = latestStockPrice.doubleValue() / eps;

            // PBR = 주가 / BPS (주당 순자산)
            double bps = fs.getTotalEquity().doubleValue() / assumedShares;
            if (bps > 0) pbr = latestStockPrice.doubleValue() / bps;
        }

        try {
            // PER 평가
            if(0 < per && per < 15) score += 10;
            else if(15 <= per && per < 25) score += 7;
            else if(25 <= per && per < 40) score += 3;

            // PBR 평가
            if(0 < pbr && pbr < 1.5) score += 10;
            else if(1.5 <= pbr && pbr < 3.0) score += 7;
            else if(3.0 <= pbr && pbr < 5.0) score += 3;
        } catch (Exception e) {
            return 0;
        }
        return score;
    }


    private double parseDouble(JsonNode node, String field) {
        if(node.has(field) && !node.get(field).asText().equalsIgnoreCase("None")) {
            try {
                return Double.parseDouble(node.get(field).asText());
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return 0.0;
    }


}

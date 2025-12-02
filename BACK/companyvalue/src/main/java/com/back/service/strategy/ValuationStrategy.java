package com.back.service.strategy;

import com.back.domain.FinancialStatement;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

@Component
public class ValuationStrategy implements ScoringStrategy {
    @Override
    public int calculate(FinancialStatement fs, JsonNode overview) {

        int score = 0;
        if (overview == null) return 0;

        try {
            double per = parseDouble(overview, "PERatio");
            double pbr = parseDouble(overview, "PriceToBookRatio");

            // PER 평가
            if (0 < per && per < 15) score += 10;
            else if (15 <= per && per < 25) score += 7;
            else if (25 <= per && per < 40) score += 3;

            // PBR 평가
            if (0 < pbr && pbr < 1.5) score += 10;
            else if (1.5 <= pbr && pbr < 3.0) score += 7;
            else if (3.0 <= pbr && pbr < 5.0) score += 3;

        } catch (Exception e) {
            return 0;
        }
        return score;
    }

    private double parseDouble(JsonNode node, String field) {
        if (node.has(field) && !node.get(field).asText().equalsIgnoreCase("None")) {
            try {
                return Double.parseDouble(node.get(field).asText());
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return 0.0;
    }
}

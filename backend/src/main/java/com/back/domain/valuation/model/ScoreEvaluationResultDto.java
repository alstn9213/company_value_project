package com.back.domain.valuation.model;

public record ScoreEvaluationResultDto(
        int totalScore,
        int stability,
        int profitability,
        int valuation,
        int investment,
        String grade,
        boolean isOpportunity
) {
}

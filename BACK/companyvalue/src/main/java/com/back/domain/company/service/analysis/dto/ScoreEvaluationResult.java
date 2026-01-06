package com.back.domain.company.service.analysis.dto;

public record ScoreEvaluationResult(
        int totalScore,
        int stability,
        int profitability,
        int valuation,
        int investment,
        String grade,
        boolean isOpportunity
) {
}

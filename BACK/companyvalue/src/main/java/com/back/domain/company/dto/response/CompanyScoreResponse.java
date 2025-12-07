package com.back.domain.company.dto.response;

import com.back.domain.company.entity.CompanyScore;

public record CompanyScoreResponse(
        String ticker,
        String name,
        Integer totalScore,
        String grade,
        Integer stabilityScore,
        Integer profitabilityScore,
        Integer valuationScore,
        Integer investmentScore,
        Boolean isOpportunity
) {
    public static CompanyScoreResponse from(CompanyScore score) {
        return new CompanyScoreResponse(
                score.getCompany().getTicker(),
                score.getCompany().getName(),
                score.getTotalScore(),
                score.getGrade(),
                score.getStabilityScore(),
                score.getProfitabilityScore(),
                score.getValuationScore(),
                score.getInvestmentScore(),
                score.getIsOpportunity() != null && score.getIsOpportunity() // null safe 처리
        );
    }
}
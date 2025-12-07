package com.back.domain.company.dto.response;

import com.back.domain.company.entity.Company;

// 1. 기업 기본 정보
public record CompanySummaryResponse(
        String ticker,
        String name,
        String sector,
        String exchange,
        Integer totalScore,
        String grade
) {
    public static CompanySummaryResponse from(Company company) {
        // 만약 점수가 아직 계산되지 않은 기업이라면 null일 수 있으므로 안전하게 처리
        int score = 0;
        String grade = "N/A";

        if (company.getCompanyScore() != null) {
            score = company.getCompanyScore().getTotalScore();
            grade = company.getCompanyScore().getGrade();
        }

        return new CompanySummaryResponse(
                company.getTicker(),
                company.getName(),
                company.getSector(),
                company.getExchange(),
                score,
                grade
        );
    }
}
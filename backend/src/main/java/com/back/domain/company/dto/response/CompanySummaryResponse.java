package com.back.domain.company.dto.response;

import com.back.domain.company.entity.Company;
import com.back.domain.company.entity.CompanyScore;

import java.util.Optional;

public record CompanySummaryResponse(
        String ticker,
        String name,
        String sector,
        String exchange,
        Integer totalScore,
        String grade
) {
  // 점수 계산이 안된 기업이 있을 땐 상수 대입
  private static final int DEFAULT_SCORE = 0;
  private static final String DEFAULT_GRADE = "N/A";

  public static CompanySummaryResponse from(Company company) {
    CompanyScore score = company.getCompanyScore();

    return new CompanySummaryResponse(
            company.getTicker(),
            company.getName(),
            company.getSector(),
            company.getExchange(),
            Optional.ofNullable(score).map(CompanyScore::getTotalScore).orElse(DEFAULT_SCORE),
            Optional.ofNullable(score).map(CompanyScore::getGrade).orElse(DEFAULT_GRADE)
    );
  }
}
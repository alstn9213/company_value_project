package com.back.domain.company.dto.response;

import com.back.domain.company.entity.Company;
import com.back.domain.company.entity.CompanyScore;

import java.util.Optional;

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
  // 점수가 있을 때
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
            Optional.ofNullable(score.getIsOpportunity()).orElse(false)
    );
  }

  // 점수가 없을 때: 회사 정보만으로 기본값 설정
  public static CompanyScoreResponse createDefault(Company company) {
    return new CompanyScoreResponse(
            company.getTicker(),
            company.getName(),
            0,
            "N/A",
            0, 0, 0, 0,
            false
    );
  }
}
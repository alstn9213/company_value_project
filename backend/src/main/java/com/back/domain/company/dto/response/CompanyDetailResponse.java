package com.back.domain.company.dto.response;

import com.back.domain.company.entity.Company;
import com.back.domain.company.entity.CompanyScore;
import com.back.domain.company.entity.FinancialStatement;

import java.util.List;

// 기업 상세 페이지용 통합 DTO (정보 + 재무 + 점수)
public record CompanyDetailResponse(
        CompanySummaryResponse companySummary,
        CompanyScoreResponse score,
        FinancialStatementResponse latestFinancial,
        List<FinancialStatementResponse> financialHistory
) {
  public static CompanyDetailResponse of(Company company, CompanyScore score, List<FinancialStatement> history) {

    // 점수 계산 여부에 따라 기본값 세팅
    CompanyScoreResponse scoreDto = (score != null)
            ? CompanyScoreResponse.from(score)
            : CompanyScoreResponse.createDefault(company);

    // 분기별 재무제표 리스트 만들기
    List<FinancialStatementResponse> historyDto = history.stream()
            .map(FinancialStatementResponse::from)
            .toList();

    // 가장 최근 재무 제표 뽑아내기
    FinancialStatementResponse latestDto = history.stream()
            .findFirst()
            .map(FinancialStatementResponse::from)
            .orElse(null);

    return new CompanyDetailResponse(
            CompanySummaryResponse.from(company),
            scoreDto,
            latestDto,
            historyDto
    );
  }
}
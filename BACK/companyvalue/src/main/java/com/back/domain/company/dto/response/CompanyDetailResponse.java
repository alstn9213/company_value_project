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
    public static CompanyDetailResponse of(Company company,
                                           CompanyScore score,
                                           List<FinancialStatement> history) {

        List<FinancialStatementResponse> historyDto = history.stream()
                .map(FinancialStatementResponse::from)
                .toList();

        FinancialStatement latest = history.isEmpty() ? new FinancialStatement() : history.get(0);

        return new CompanyDetailResponse(
                CompanySummaryResponse.from(company),
                CompanyScoreResponse.from(score),
                FinancialStatementResponse.from(latest),
                historyDto
        );
    }
}
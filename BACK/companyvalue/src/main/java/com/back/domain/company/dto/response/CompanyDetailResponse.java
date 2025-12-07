package com.back.domain.company.dto.response;

import com.back.infra.external.dto.ExternalFinancialDataResponse;

import java.util.List;

// 기업 상세 페이지용 통합 DTO (정보 + 재무 + 점수)
public record CompanyDetailResponse(
        CompanySummaryResponse companySummary,
        CompanyScoreResponse score,
        FinancialStatementResponse latestFinancial,
        List<FinancialStatementResponse> financialHistory
) {}
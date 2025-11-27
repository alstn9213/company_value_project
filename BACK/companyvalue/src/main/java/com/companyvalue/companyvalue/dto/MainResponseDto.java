package com.companyvalue.companyvalue.dto;

import com.companyvalue.companyvalue.domain.Company;
import com.companyvalue.companyvalue.domain.CompanyScore;
import com.companyvalue.companyvalue.domain.FinancialStatement;
import com.companyvalue.companyvalue.domain.MacroEconomicData;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MainResponseDto {

    // 1. 기업 기본 정보
    public record CompanyInfo(
            String ticker,
            String name,
            String sector,
            String exchange
    ) {
        public static CompanyInfo from(Company company) {
            return new CompanyInfo(
                    company.getTicker(),
                    company.getName(),
                    company.getSector(),
                    company.getExchange()
            );
        }
    }

    // 2. 기업 점수 및 등급
    public record ScoreResult(
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
        public static ScoreResult from(CompanyScore score) {
            return new ScoreResult(
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
    // 3. 재무제표 상세
    public record FinancialDetail(
            Integer year,
            Integer quarter,
            BigDecimal revenue,
            BigDecimal operatingProfit,
            BigDecimal netIncome,
            BigDecimal totalAssets,
            BigDecimal totalLiabilities,
            BigDecimal totalEquity,
            BigDecimal operatingCashFlow,
            BigDecimal researchAndDevelopment,
            BigDecimal capitalExpenditure
    ) {
        public static FinancialDetail from(FinancialStatement fs) {
            return new FinancialDetail(
                    fs.getYear(),
                    fs.getQuarter(),
                    fs.getRevenue(),
                    fs.getOperatingProfit(),
                    fs.getNetIncome(),
                    fs.getTotalAssets(),
                    fs.getTotalLiabilities(),
                    fs.getTotalEquity(),
                    fs.getOperatingCashFlow(),
                    fs.getResearchAndDevelopment(),
                    fs.getCapitalExpenditure()
            );
        }
    }

    // 4. 기업 상세 페이지용 통합 DTO (정보 + 최근 재무 + 점수)
    public record CompanyDetailResponse(
            CompanyInfo info,
            ScoreResult score,
            FinancialDetail latestFinancial
    ) {}

    // 5. 거시 경제 지표
    public record MacroDataResponse(
            LocalDate date,
            Double fedFundsRate, // 기준금리
            Double us10y,        // 10년물 국채
            Double us2y,         // 2년물 국채
            Double spread,       // 장단기 금리차 (10y - 2y)
            Double inflation,    // 인플레이션
            Double unemployment  // 실업률
    ) {
        public static MacroDataResponse from(MacroEconomicData macro) {
            // null 방지 로직
            double y10 = macro.getUs10yTreasuryYield() != null ? macro.getUs10yTreasuryYield() : 0.0;
            double y2 = macro.getUs2yTreasuryYield() != null ? macro.getUs2yTreasuryYield() : 0.0;

            return new MacroDataResponse(
                    macro.getRecordedDate(),
                    macro.getFedFundsRate(),
                    y10,
                    y2,
                    Math.round((y10 - y2) * 100.0) / 100.0, // 소수점 2자리 반올림
                    macro.getInflationRate(),
                    macro.getUnemploymentRate()
            );
        }
    }

    // 6. 관심 종목 응답
    public record WatchlistResponse(
            Long watchlistId,
            String ticker,
            String name,
            Integer currentScore,
            String currentGrade
    ) {}
}

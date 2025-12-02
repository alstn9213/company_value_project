package com.companyvalue.companyvalue.service;

import com.companyvalue.companyvalue.domain.Company;
import com.companyvalue.companyvalue.domain.CompanyScore;
import com.companyvalue.companyvalue.domain.FinancialStatement;
import com.companyvalue.companyvalue.domain.MacroEconomicData;
import com.companyvalue.companyvalue.domain.repository.CompanyRepository;
import com.companyvalue.companyvalue.domain.repository.CompanyScoreRepository;
import com.companyvalue.companyvalue.domain.repository.MacroRepository;
import com.companyvalue.companyvalue.dto.MainResponseDto;
import com.companyvalue.companyvalue.service.strategy.InvestmentStrategy;
import com.companyvalue.companyvalue.service.strategy.ProfitabilityStrategy;
import com.companyvalue.companyvalue.service.strategy.StabilityStrategy;
import com.companyvalue.companyvalue.service.strategy.ValuationStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScoringService {

    private final CompanyScoreRepository companyScoreRepository;
    private final MacroRepository macroRepository;
    private final CompanyRepository companyRepository;

    private final StabilityStrategy stabilityStrategy;
    private final ProfitabilityStrategy profitabilityStrategy;
    private final ValuationStrategy valuationStrategy;
    private final InvestmentStrategy investmentStrategy;

    private static final String SECTOR_FINANCIAL = "Financial Services";

    /**
     * 기업의 재무제표와 현재 시장 상황을 기반으로 점수를 계산하고 저장합니다.
     */
    @Transactional
    public void calculateAndSaveScore(FinancialStatement fs, JsonNode overview) {
        MacroEconomicData macro = macroRepository.findTopByOrderByRecordedDateDesc()
                .orElseThrow(() -> new RuntimeException("거시 경제 데이터가 없습니다."));

        // 점수 계산
        int stability = stabilityStrategy.calculate(fs, overview);
        int profitability = profitabilityStrategy.calculate(fs, overview);
        int valuation = valuationStrategy.calculate(fs, overview);
        int investment = investmentStrategy.calculate(fs, overview);
        int totalScore = stability + profitability + valuation + investment;
        String grade;
        // 저점 매수 판단
        boolean isOpportunity = false;
        // 과락 체크
        if (isDisqualified(fs)) {
            // 과락인 경우: 세부 점수는 유지하되, 총점은 0점, 등급은 F로 강제
            totalScore = 0;
            grade = "F";
        } else {
            // 페널티 및 점수 보정
            int macroPenalty = applyMacroPenalty(macro);
            int riskyPenalty = applyRiskyInvestmentPenalty(fs, macro);

            totalScore = totalScore - macroPenalty - riskyPenalty;
            totalScore = Math.max(0, Math.min(100, totalScore)); // 점수를 0 ~ 100 범위로 유지

            grade = calculateGrade(totalScore);
            isOpportunity = (macroPenalty > 0) && (valuation >= 20);
        }

        saveScore(
                fs,
                totalScore,
                stability,
                profitability,
                valuation,
                investment,
                grade,
                isOpportunity
        );
    }

    // --- [과락 체크] ---
    // 부채비율 400% 초과 혹은 자본잠식(자본 < 0)
    private boolean isDisqualified(FinancialStatement fs) {
        BigDecimal equity = fs.getTotalEquity();
        BigDecimal liabilities = fs.getTotalLiabilities();

        // 자본 잠식
        if(equity.compareTo(BigDecimal.ZERO) <= 0) return true;

        // 부채비율 400% 초과
        BigDecimal debtRatio = liabilities.divide(equity, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        boolean isFinance = isFinancialSector(fs.getCompany());
        double limit = isFinance ? 1500.0 : 400.0;

        if(debtRatio.doubleValue() > limit) return true;

        return false;
    }

    // --- [페널티 1] 경기 침체시 기업의 재무 상태에 따라 페널티를 차등 적용 ---
    private int applyMacroPenalty(MacroEconomicData macro) {
        // 장단기 금리차 역전 (10년물 < 2년물) 시 경기 침체 신호로 간주
        if (macro.getUs10yTreasuryYield() != null && macro.getUs2yTreasuryYield() != null) {
            if (macro.getUs10yTreasuryYield() < macro.getUs2yTreasuryYield()) {
                log.info("페널티가 적용되었습니다.: 장단기 금리차 역전");
                return 10;
            }
        }
        return 0;
    }

    // --- [페널티 2] 고금리 시기 위험 투자 (-15점) ---
    private int applyRiskyInvestmentPenalty(FinancialStatement fs, MacroEconomicData macro) {
        if (macro.getUs10yTreasuryYield() == null) return 0;
        // 1. 고금리 기준: 10년물 국채 금리 4.0% 이상
        boolean isHighInterest = macro.getUs10yTreasuryYield() >= 4.0;
        if (!isHighInterest) return 0;
        // 2. 부채비율 높음 (200% 이상 가정)
        BigDecimal equity = fs.getTotalEquity();
        if (equity.compareTo(BigDecimal.ZERO) == 0) return 0;

        BigDecimal debtRatio = fs.getTotalLiabilities().divide(equity, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        boolean isFinance = isFinancialSector(fs.getCompany());
        double debtThreshold = isFinance ? 1000.0 : 200.0;
        boolean isHighDebt = debtRatio.doubleValue() >= debtThreshold;

        // 3. 공격적 투자 (매출 대비 10% 이상)
        BigDecimal revenue = fs.getRevenue();
        if (revenue.compareTo(BigDecimal.ZERO) == 0) return 0;

        BigDecimal invest = (fs.getResearchAndDevelopment() != null ? fs.getResearchAndDevelopment() : BigDecimal.ZERO)
                .add(fs.getCapitalExpenditure() != null ? fs.getCapitalExpenditure() : BigDecimal.ZERO);

        double investRatio = invest.divide(revenue, 4, RoundingMode.HALF_UP).doubleValue() * 100;
        boolean isAggressive = investRatio >= 10.0;

        if (isHighDebt && isAggressive) {
            return 15;
        }
        return 0;
    }

    private boolean isFinancialSector(Company company) {
        return SECTOR_FINANCIAL.equalsIgnoreCase(company.getSector());
    }

    private String calculateGrade(int score) {
        if(score >= 90) return "S";
        if(score >= 80) return "A";
        if(score >= 70) return "B";
        if(score >= 50) return "C";
        return "D";
    }

    private void saveScore(
            FinancialStatement fs,
            int total,
            int stab,
            int prof,
            int val,
            int inv,
            String grade,
            boolean isOpportunity
    ) {
        CompanyScore score = companyScoreRepository.findByCompany(fs.getCompany())
                .orElseGet(() -> CompanyScore.builder()
                        .company(fs.getCompany())
                        .build());

        score.updateScore(
                total,
                stab,
                prof,
                val,
                inv,
                grade,
                isOpportunity
        );

        companyScoreRepository.save(score);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "company_score", key = "#ticker", unless = "#result == null")
    public MainResponseDto.ScoreResult getScoreByTicker(String ticker) {
        Company company = companyRepository.findByTicker(ticker)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 기업입니다."));

        return companyScoreRepository.findByCompany(company)
                .map(MainResponseDto.ScoreResult::from)
                .orElse(null);
    }
}
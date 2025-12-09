package com.back.domain.company.service.analysis;

import com.back.domain.company.dto.response.CompanyScoreResponse;
import com.back.domain.company.entity.Company;
import com.back.domain.company.entity.CompanyScore;
import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.entity.StockPriceHistory;
import com.back.domain.company.repository.StockPriceHistoryRepository;
import com.back.domain.company.service.analysis.constant.ScoringConstants;
import com.back.domain.company.service.analysis.policy.PenaltyPolicy;
import com.back.domain.company.service.analysis.strategy.InvestmentStrategy;
import com.back.domain.company.service.analysis.strategy.ProfitabilityStrategy;
import com.back.domain.company.service.analysis.strategy.StabilityStrategy;
import com.back.domain.company.service.analysis.strategy.ValuationStrategy;
import com.back.domain.macro.entity.MacroEconomicData;
import com.back.domain.company.repository.CompanyRepository;
import com.back.domain.company.repository.CompanyScoreRepository;
import com.back.domain.macro.repository.MacroRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static com.back.domain.company.service.analysis.constant.ScoringConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScoringService {

    private final CompanyScoreRepository companyScoreRepository;
    private final MacroRepository macroRepository;
    private final CompanyRepository companyRepository;
    private final StockPriceHistoryRepository stockPriceHistoryRepository;

    // Strategies (점수 계산 전략)
    private final StabilityStrategy stabilityStrategy;
    private final ProfitabilityStrategy profitabilityStrategy;
    private final ValuationStrategy valuationStrategy;
    private final InvestmentStrategy investmentStrategy;

    // Policies (페널티 정책)
    private final PenaltyPolicy penaltyPolicy;

    @Transactional
    public void calculateAndSaveScore(FinancialStatement fs, JsonNode overview) {
        MacroEconomicData macro = macroRepository.findTopByOrderByRecordedDateDesc()
                .orElseThrow(() -> new RuntimeException("거시 경제 데이터가 없습니다."));

        BigDecimal currentPrice = BigDecimal.ZERO;
        StockPriceHistory latestStock = stockPriceHistoryRepository.findTopByCompanyOrderByRecordedDateDesc(fs.getCompany());
        if(latestStock != null) currentPrice = latestStock.getClosePrice();

        // 기본 점수 계산 (Strategy Pattern 활용)
        int stability = stabilityStrategy.calculate(fs, overview);
        int profitability = profitabilityStrategy.calculate(fs, overview);
        int valuation = valuationStrategy.calculate(fs, overview, currentPrice);
        int investment = investmentStrategy.calculate(fs, overview);

        int baseScore = stability + profitability + valuation + investment;
        int penalty = penaltyPolicy.calculatePenalty(fs, macro);

        int totalScore = Math.max(0, Math.min(100, baseScore - penalty));
        String grade = calculateGrade(totalScore);

        // 페널티는 존재하지만, 기업 자체의 가치는 훌륭해서(PBR, PER이 높음) 저점 매수하기 좋을 경우
        // 단, 자본 잠식은 걸러냄
        boolean isOpportunity = (penalty > 0)
                && (valuation >= OPPORTUNITY_VALUATION_THRESHOLD)
                && (fs.getTotalEquity().compareTo(BigDecimal.ZERO) > 0);

        saveScore(fs, totalScore, stability, profitability, valuation, investment, grade, isOpportunity);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "company_score", key = "#ticker", unless = "#result == null")
    public CompanyScoreResponse getScoreByTicker(String ticker) {
        Company company = companyRepository.findByTicker(ticker)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 기업입니다."));

        return companyScoreRepository.findByCompany(company)
                .map(CompanyScoreResponse::from)
                .orElse(null);
    }

    // --- 내부 메서드 ---

    private String calculateGrade(int score) {
        if(score >= GRADE_S_THRESHOLD) return "S";
        if(score >= GRADE_A_THRESHOLD) return "A";
        if(score >= GRADE_B_THRESHOLD) return "B";
        if(score >= GRADE_C_THRESHOLD) return "C";
        return "D";
    }

    private void saveScore(FinancialStatement fs, int total, int stab, int prof, int val, int inv, String grade, boolean isOpportunity) {
        CompanyScore score = companyScoreRepository.findByCompany(fs.getCompany())
                .orElseGet(() -> CompanyScore.builder()
                        .company(fs.getCompany())
                        .build());

        score.updateScore(total, stab, prof, val, inv, grade, isOpportunity);
        companyScoreRepository.save(score);
    }
}
package com.back.domain.company.service.analysis;

import com.back.domain.company.dto.response.CompanyScoreResponse;
import com.back.domain.company.entity.Company;
import com.back.domain.company.entity.CompanyScore;
import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.service.analysis.constant.ScoringConstants;
import com.back.domain.company.service.analysis.policy.DisqualificationPolicy;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class ScoringService {

    private final CompanyScoreRepository companyScoreRepository;
    private final MacroRepository macroRepository;
    private final CompanyRepository companyRepository;

    // Strategies (점수 계산 전략)
    private final StabilityStrategy stabilityStrategy;
    private final ProfitabilityStrategy profitabilityStrategy;
    private final ValuationStrategy valuationStrategy;
    private final InvestmentStrategy investmentStrategy;

    // Policies (비즈니스 규칙 정책)
    private final DisqualificationPolicy disqualificationPolicy;
    private final PenaltyPolicy penaltyPolicy;

    /**
     * 기업의 재무제표와 현재 시장 상황을 기반으로 점수를 계산하고 저장합니다.
     */
    @Transactional
    public void calculateAndSaveScore(FinancialStatement fs, JsonNode overview) {
        // 1. 거시 경제 데이터 조회
        MacroEconomicData macro = macroRepository.findTopByOrderByRecordedDateDesc()
                .orElseThrow(() -> new RuntimeException("거시 경제 데이터가 없습니다."));

        // 2. 기본 점수 계산 (Strategy Pattern 활용)
        int stability = stabilityStrategy.calculate(fs, overview);
        int profitability = profitabilityStrategy.calculate(fs, overview);
        int valuation = valuationStrategy.calculate(fs, overview);
        int investment = investmentStrategy.calculate(fs, overview);

        int totalScore = stability + profitability + valuation + investment;
        String grade;
        boolean isOpportunity = false;

        // 3. 과락 및 페널티 적용 (Policy Pattern 활용)
        if (disqualificationPolicy.isDisqualified(fs)) {
            totalScore = 0;
            grade = "F";
        } else {
            int penalty = penaltyPolicy.calculatePenalty(fs, macro);

            totalScore = Math.max(0, Math.min(100, totalScore - penalty)); // 0~100 범위 보정
            grade = calculateGrade(totalScore);

            // 기회 여부: 페널티가 존재하지만(경기 침체 등) 가치 점수가 높은 경우
            isOpportunity = (penalty > 0) && (valuation >= ScoringConstants.OPPORTUNITY_VALUATION_THRESHOLD);
        }

        // 4. 저장
        saveScore(fs, totalScore, stability, profitability, valuation, investment, grade, isOpportunity);
    }

    private String calculateGrade(int score) {
        if (score >= ScoringConstants.GRADE_S_THRESHOLD) return "S";
        if (score >= ScoringConstants.GRADE_A_THRESHOLD) return "A";
        if (score >= ScoringConstants.GRADE_B_THRESHOLD) return "B";
        if (score >= ScoringConstants.GRADE_C_THRESHOLD) return "C";
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

    @Transactional(readOnly = true)
    @Cacheable(value = "company_score", key = "#ticker", unless = "#result == null")
    public CompanyScoreResponse getScoreByTicker(String ticker) {
        Company company = companyRepository.findByTicker(ticker)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 기업입니다."));

        return companyScoreRepository.findByCompany(company)
                .map(CompanyScoreResponse::from)
                .orElse(null);
    }
}
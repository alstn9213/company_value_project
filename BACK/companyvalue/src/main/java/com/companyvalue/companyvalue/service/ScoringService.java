package com.companyvalue.companyvalue.service;

import com.companyvalue.companyvalue.domain.CompanyScore;
import com.companyvalue.companyvalue.domain.FinancialStatement;
import com.companyvalue.companyvalue.domain.MacroEconomicData;
import com.companyvalue.companyvalue.domain.repository.CompanyScoreRepository;
import com.companyvalue.companyvalue.domain.repository.MacroRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * 기업의 재무제표와 현재 시장 상황을 기반으로 점수를 계산하고 저장합니다.
     */
    @Transactional
    public void calculateAndSaveScore(FinancialStatement fs, JsonNode overview) {
        String ticker = fs.getCompany().getTicker();
        log.info("회사 점수 계산 시작(DB I/O): {}", fs.getCompany().getTicker());

        //  1. 최신 거시 경제 지표 가져오기
        MacroEconomicData macro = macroRepository.findTopByOrderByRecordedDateDesc()
                .orElseThrow(() -> new RuntimeException("거시 경제 데이터가 없습니다."));

      // 2. 과락 체크 - 하나라도 걸리면 0점 처리
        if(isDisqualified(fs)) {
            saveScore(fs, 0, 0, 0, 0, 0, "F (과락)");
            return;
        }

      // 3. 각 항목별 점수 계산
        int stability = calculateStability(fs);       // 40점 만점
        int profitability = calculateProfitability(fs); // 30점 만점
        int valuation = calculateValuation(overview); // 20점 만점
        int investment = calculateInvestment(fs);     // 10점 만점

        int totalScore = stability + profitability + valuation + investment;

        // 4. 동적 페널티 및 보너스 적용
        // 4-1. 거시 경제 악화 페널티 (-10)
        int macroPenalty = applyMacroPenalty(macro);

        // 4-2. 고금리 시기 위험 투자 페널티 (-15)
        int riskyPenalty = applyRiskyInvestmentPenalty(fs, macro);

        totalScore = totalScore - macroPenalty - riskyPenalty;

        // 5. 점수 보정 (0 ~ 100 범위 유지)
        totalScore = Math.max(0, Math.min(100, totalScore));

        // 6. 등급 산정
        String grade = calculateGrade(totalScore);

        // 7. 결과 저장
        saveScore(fs, totalScore, stability, profitability, valuation, investment, grade);

        log.info("계산 완료 총점: {}, 등급: {}", totalScore, grade);
    }

    // --- [1] 안정성 평가 (40점 만점) ---
    // 기준: 부채비율(Debt Ratio)과 영업활동현금흐름
    private int calculateStability(FinancialStatement fs) {
        BigDecimal totalLiabilities = fs.getTotalLiabilities();
        BigDecimal totalEquity = fs.getTotalEquity();

        int score = 0;

        // 1. 부채비율 계산 (부채 / 자본 * 100)
        if (totalEquity.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal debtRatio = totalLiabilities.divide(totalEquity, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));

            // 부채비율에 따른 점수 (20점 만점)
            if (debtRatio.compareTo(BigDecimal.valueOf(100)) < 0) score += 20;      // 100% 미만
            else if (debtRatio.compareTo(BigDecimal.valueOf(200)) < 0) score += 10; // 200% 미만
            else if (debtRatio.compareTo(BigDecimal.valueOf(300)) < 0) score += 5;  // 300% 미만
            // 300% 이상은 0점
        }

        // 2. 영업활동 현금흐름 (20점 만점)
        // 흑자면 20점, 적자면 0점 (단순화)
        if (fs.getOperatingCashFlow().compareTo(BigDecimal.ZERO) > 0) {
            score += 20;
        }

        return score;
        }

    // --- [2] 수익성 평가 (30점 만점) ---
    // 기준: ROE(자기자본이익률), 영업이익률
    private int calculateProfitability(FinancialStatement fs) {
        int score = 0;
        BigDecimal netIncome = fs.getNetIncome();
        BigDecimal equity = fs.getTotalEquity();
        BigDecimal revenue = fs.getRevenue();
        BigDecimal operatingProfit = fs.getOperatingProfit();

        // 1. ROE (당기순이익 / 자본 * 100) - 15점
        if (equity.compareTo(BigDecimal.ZERO) > 0) {
            double roe = netIncome.divide(equity, 4, RoundingMode.HALF_UP).doubleValue() * 100;
            if (roe >= 20) score += 15;
            else if (roe >= 10) score += 10;
            else if (roe >= 0) score += 5;
        }

        // 2. 영업이익률 (영업이익 / 매출 * 100) - 15점
        if (revenue.compareTo(BigDecimal.ZERO) > 0) {
            double opMargin = operatingProfit.divide(revenue, 4, RoundingMode.HALF_UP).doubleValue() * 100;
            if (opMargin >= 20) score += 15;
            else if (opMargin >= 10) score += 10;
            else if (opMargin >= 0) score += 5;
        }
        return score;
    }

    // --- [3] 가치 평가 (20점 만점) ---
    // API에서 가져온 PER, PBR 데이터를 사용
    private int calculateValuation(JsonNode overview) {
        int score = 0;
        try {
            double per = parseDouble(overview, "PERatio");
            double pbr = parseDouble(overview, "PriceToBookRatio");

            // PER 평가 (낮을수록 저평가)
            // 기술주(나스닥) 위주라면 기준을 조금 높게 잡아도 됩니다.
            if (per > 0 && per < 15) score += 10;
            else if (per >= 15 && per < 25) score += 7;
            else if (per >= 25 && per < 40) score += 3;
            // PER이 0 이하(적자)거나 너무 높으면 0점

            // PBR 평가 (낮을수록 저평가)
            if (pbr > 0 && pbr < 1.5) score += 10;
            else if (pbr >= 1.5 && pbr < 3.0) score += 7;
            else if (pbr >= 3.0 && pbr < 5.0) score += 3;

        } catch (Exception e) {
            log.warn("가치 평가 실패: {}", e.getMessage());
            // 데이터가 없거나 에러 시 기본 점수 부여 혹은 0점
            return 5;
        }
        return score;
    }

    // --- [4] 미래 투자 적극성 (10점 만점) ---
    // 기준: 매출액 대비 (R&D + CapEx) 비율
    private int calculateInvestment(FinancialStatement fs) {
        BigDecimal revenue = fs.getRevenue();
        if(revenue.compareTo(BigDecimal.ZERO) == 0) return 0;

        BigDecimal rnd = fs.getResearchAndDevelopment() != null ? fs.getResearchAndDevelopment() : BigDecimal.ZERO;
        BigDecimal capex = fs.getCapitalExpenditure() != null ? fs.getCapitalExpenditure() : BigDecimal.ZERO;
        BigDecimal investmentSum = rnd.add(capex);
        double ratio = investmentSum.divide(revenue, 4, RoundingMode.HALF_UP).doubleValue() * 100;

        // 매출의 15% 이상 투자 시 만점
        if (ratio >= 15) return 10;
        else if (ratio >= 10) return 7;
        else if (ratio >= 5) return 3;

        return 0;
    }

    // --- [과락 체크] ---
    // 부채비율 400% 초과 혹은 자본잠식(자본 < 0)
    private boolean isDisqualified(FinancialStatement fs) {
        BigDecimal equity = fs.getTotalEquity();
        BigDecimal liabilities = fs.getTotalLiabilities();

        // 자본 잠식
        if (equity.compareTo(BigDecimal.ZERO) <= 0) return true;

        // 부채비율 400% 초과
        BigDecimal debtRatio = liabilities.divide(equity, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        if (debtRatio.compareTo(BigDecimal.valueOf(400)) > 0) return true;

        return false;
    }

    // --- [페널티 1] 거시 경제 악화 (-10점) ---
    private int applyMacroPenalty(MacroEconomicData macro) {
        // 장단기 금리차 역전 (10년물 < 2년물) 시 경기 침체 신호로 간주
        if (macro.getUs10yTreasuryYield() != null && macro.getUs2yTreasuryYield() != null) {
            if (macro.getUs10yTreasuryYield() < macro.getUs2yTreasuryYield()) {
                log.info("Penalty applied: Inverted Yield Curve");
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
        boolean isHighDebt = debtRatio.doubleValue() >= 200.0;

        // 3. 공격적 투자 (매출 대비 10% 이상)
        BigDecimal revenue = fs.getRevenue();
        if (revenue.compareTo(BigDecimal.ZERO) == 0) return 0;

        BigDecimal invest = (fs.getResearchAndDevelopment() != null ? fs.getResearchAndDevelopment() : BigDecimal.ZERO)
                .add(fs.getCapitalExpenditure() != null ? fs.getCapitalExpenditure() : BigDecimal.ZERO);

        double investRatio = invest.divide(revenue, 4, RoundingMode.HALF_UP).doubleValue() * 100;
        boolean isAggressive = investRatio >= 10.0;

        if (isHighDebt && isAggressive) {
            log.info("Penalty applied: Risky Investment in High Interest Era");
            return 15;
        }

        return 0;
    }

    // 등급 계산 헬퍼
    private String calculateGrade(int score) {
        if(score >= 90) return "S";
        if(score >= 80) return "A";
        if(score >= 70) return "B";
        if(score >= 50) return "C";
        return "D";
    }

    // DB 저장 로직
    private void saveScore(FinancialStatement fs, int total, int stab, int prof, int val, int inv, String grade) {
        CompanyScore score = companyScoreRepository.findByCompany(fs.getCompany())
                .orElseGet(() -> CompanyScore.builder()
                        .company(fs.getCompany())
                        .build()); // CompanyScore에 @Builder 적용 가정

        score.updateScore(total, stab, prof, val, inv, grade); // CompanyScore에 update 메서드 추가 필요
        companyScoreRepository.save(score);
    }

    // JSON 파싱 헬퍼
    private double parseDouble(JsonNode node, String field) {
        if (node.has(field) && !node.get(field).asText().equalsIgnoreCase("None")) {
            try {
                return Double.parseDouble(node.get(field).asText());
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return 0.0;
    }
}
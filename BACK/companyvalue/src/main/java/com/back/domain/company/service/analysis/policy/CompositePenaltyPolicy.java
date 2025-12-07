package com.back.domain.company.service.analysis.policy;

import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.service.analysis.constant.ScoringConstants;
import com.back.domain.macro.entity.MacroEconomicData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Component
public class CompositePenaltyPolicy implements PenaltyPolicy{

    @Override
    public int calculatePenalty(FinancialStatement fs, MacroEconomicData macro) {
        if (macro == null) return 0;

        int macroPenalty = calculateMacroPenalty(macro);
        int riskPenalty = calculateRiskyInvestmentPenalty(fs, macro);

        return macroPenalty + riskPenalty;
    }

    // [페널티 1] 장단기 금리차 역전
    private int calculateMacroPenalty(MacroEconomicData macro) {
        if (macro.getUs10yTreasuryYield() != null && macro.getUs2yTreasuryYield() != null) {
            if (macro.getUs10yTreasuryYield() < macro.getUs2yTreasuryYield()) {
                log.debug("페널티 적용: 장단기 금리차 역전");
                return ScoringConstants.PENALTY_SCORE_MACRO;
            }
        }
        return 0;
    }

    // [페널티 2] 고금리 시기 위험 투자
    private int calculateRiskyInvestmentPenalty(FinancialStatement fs, MacroEconomicData macro) {
        if (macro.getUs10yTreasuryYield() == null) return 0;

        if (macro.getUs10yTreasuryYield() < ScoringConstants.HIGH_INTEREST_RATE_THRESHOLD) {
            return 0;
        }

        if (isHighDebt(fs)) {
            if (isAggressiveInvestment(fs)) {
                log.debug("페널티 적용: 고금리/고부채 하에서의 공격적 투자");
                return ScoringConstants.PENALTY_SCORE_RISKY_INVESTMENT;
            }
        }
        return 0;
    }

    private boolean isHighDebt(FinancialStatement fs) {
        BigDecimal equity = fs.getTotalEquity();
        if (equity.compareTo(BigDecimal.ZERO) == 0) return false;

        BigDecimal debtRatio = fs.getTotalLiabilities().divide(equity, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        boolean isFinance = ScoringConstants.SECTOR_FINANCIAL.equalsIgnoreCase(fs.getCompany().getSector());
        double threshold = isFinance ? ScoringConstants.HIGH_DEBT_RATIO_FINANCIAL : ScoringConstants.HIGH_DEBT_RATIO_GENERAL;

        return debtRatio.doubleValue() >= threshold;
    }

    private boolean isAggressiveInvestment(FinancialStatement fs) {
        BigDecimal revenue = fs.getRevenue();
        if (revenue.compareTo(BigDecimal.ZERO) == 0) return false;

        BigDecimal invest = (fs.getResearchAndDevelopment() != null ? fs.getResearchAndDevelopment() : BigDecimal.ZERO)
                .add(fs.getCapitalExpenditure() != null ? fs.getCapitalExpenditure() : BigDecimal.ZERO);

        double investRatio = invest.divide(revenue, 4, RoundingMode.HALF_UP).doubleValue() * 100;
        return investRatio >= ScoringConstants.AGGRESSIVE_INVESTMENT_RATIO;
    }

}

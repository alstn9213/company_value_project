package com.back.domain.company.service.analysis.policy;

import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.macro.entity.MacroEconomicData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.back.domain.company.service.analysis.constant.ScoringConstants.*;

@Slf4j
@Component
public class CompositePenaltyPolicy implements PenaltyPolicy {

    @Override
    public int calculatePenalty(FinancialStatement fs, MacroEconomicData macro) {
        int totalPenalty = 0;
        if(macro != null) {
            totalPenalty += calculateMacroPenalty(macro);
            totalPenalty += calculateRiskyInvestmentPenalty(fs, macro);
        }
        totalPenalty += calculateFinancialStructurePenalty(fs);
        return totalPenalty;
    }

    // --- 내부 메서드 ---

    // 자본 잠식이거나 부채 비율이 과도하면 감점
    private int calculateFinancialStructurePenalty(FinancialStatement fs) {
        BigDecimal equity = fs.getTotalEquity();
        BigDecimal liabilities = fs.getTotalLiabilities();
        int penalty = 0;

        // 자본 잠식이면 바로 감점
        if(equity.compareTo(BigDecimal.ZERO) <= 0) {
            log.debug("페널티 적용: 자본 잠식(기업: {})", fs.getCompany().getName());
            return PENALTY_SCORE_CAPITAL_IMPAIRMENT;
        }

        // 금융 기업은 일반 기업에 비해 부채 비율 기준 완화
        boolean isFinancial = SECTOR_FINANCIAL.equalsIgnoreCase(fs.getCompany().getSector());
        BigDecimal limitRatio = isFinancial
                ? BigDecimal.valueOf(DEBT_RATIO_LIMIT_FINANCIAL)
                : BigDecimal.valueOf(DEBT_RATIO_LIMIT_GENERAL);

        BigDecimal debtRatio = liabilities.divide(equity, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));

        // 부채 비율이 높으면 감점
        if(debtRatio.compareTo(limitRatio) > 0) {
            log.debug("페널티 적용: 과도한 부채비율(기업: {}, 업종: {}, 비율: {}%, 기준: {}%)",
                    fs.getCompany().getName(),
                    isFinancial ? "금융" : "일반",
                    debtRatio,
                    limitRatio);
            penalty += PENALTY_SCORE_EXCESSIVE_DEBT;
        }
        return penalty;
    }

    // 장단기 금리차 역전 페널티(경제 침체시 모든 기업에 감점)
    private int calculateMacroPenalty(MacroEconomicData macro) {
        if(macro.getUs10yTreasuryYield() != null && macro.getUs2yTreasuryYield() != null) {
            if(macro.getUs10yTreasuryYield() < macro.getUs2yTreasuryYield()) {
                log.debug("페널티 적용: 장단기 금리차 역전");
                return PENALTY_SCORE_MACRO;
            }
        }
        return 0;
    }

    // 고금리인데 기업이 투자를 확대할 경우 감점
    private int calculateRiskyInvestmentPenalty(FinancialStatement fs, MacroEconomicData macro) {
        if(macro.getUs10yTreasuryYield() == null) return 0;
        if(macro.getUs10yTreasuryYield() < HIGH_INTEREST_RATE_THRESHOLD) return 0;

        if(exceedsDebtRatio(fs, HIGH_DEBT_RATIO_GENERAL, HIGH_DEBT_RATIO_FINANCIAL)) {
            if(isAggressiveInvestment(fs)) {
                log.debug("페널티 적용: 고금리/고부채 하에서의 공격적 투자");
                return PENALTY_SCORE_RISKY_INVESTMENT;
            }
        }
        return 0;
    }

    private boolean exceedsDebtRatio(FinancialStatement fs, double generalLimit, double financialLimit) {
        BigDecimal equity = fs.getTotalEquity();
        // 자본이 0이면 부채로 나눌 때 오류 발생하니까 false 반환
        // 자본이 0보다 낮을 때의 처리는 다른 메서드가 담당
        if(equity.compareTo(BigDecimal.ZERO) == 0) return false;

        BigDecimal liabilities = fs.getTotalLiabilities();
        BigDecimal debtRatio = liabilities.divide(equity, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        boolean isFinance = SECTOR_FINANCIAL.equalsIgnoreCase(fs.getCompany().getSector());
        double threshold = isFinance
                ? financialLimit
                : generalLimit;

        return debtRatio.doubleValue() > threshold;
    }

    private boolean isAggressiveInvestment(FinancialStatement fs) {
        BigDecimal revenue = fs.getRevenue();
        if(revenue.compareTo(BigDecimal.ZERO) == 0) return false;

        BigDecimal invest = (
                fs.getResearchAndDevelopment() != null
                ? fs.getResearchAndDevelopment()
                : BigDecimal.ZERO
        )
                .add(fs.getCapitalExpenditure() != null
                        ? fs.getCapitalExpenditure()
                        : BigDecimal.ZERO);

        double investRatio = invest.divide(revenue, 4, RoundingMode.HALF_UP).doubleValue() * 100;
        return investRatio >= AGGRESSIVE_INVESTMENT_RATIO;
    }

}

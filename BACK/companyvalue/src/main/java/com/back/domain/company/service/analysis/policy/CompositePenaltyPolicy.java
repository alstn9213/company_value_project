package com.back.domain.company.service.analysis.policy;

import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.macro.entity.MacroEconomicData;
import com.back.global.error.ErrorCode;
import com.back.global.error.exception.BusinessException;
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
        if(macro == null) {
            log.error("패널티 계산 실패: 거시 경제 데이터가 존재하지 않습니다.");
            throw new BusinessException(ErrorCode.MACRO_DATA_NOT_FOUND);
        }

        int totalPenalty = 0;
        totalPenalty += calculateFinancialStructurePenalty(fs);
        totalPenalty += calculateMacroPenalty(macro);
        totalPenalty += calculateRiskyInvestmentPenalty(fs, macro);

        return totalPenalty;
    }

    // --- 내부 메서드 ---

    // 자본 잠식이거나 부채 비율이 과도하면 감점
    private int calculateFinancialStructurePenalty(FinancialStatement fs) {
        BigDecimal equity = fs.getTotalEquity(); // 자본
        BigDecimal liabilities = fs.getTotalLiabilities(); // 부채
        int penalty = 0;

        // 자본 잠식이면 40점 감점
        if(equity.compareTo(BigDecimal.ZERO) <= 0) {
            log.debug("페널티 적용: 자본 잠식(기업: {})", fs.getCompany().getName());
            return PENALTY_SCORE_CAPITAL_IMPAIRMENT;
        }

        // 금융 기업은 일반 기업에 비해 부채 비율 기준 완화
        boolean isFinancial = SECTOR_FINANCIAL.equalsIgnoreCase(fs.getCompany().getSector());
        BigDecimal limitRatio = isFinancial
                ? BigDecimal.valueOf(HIGH_DEBT_RATIO_FINANCIAL)
                : BigDecimal.valueOf(HIGH_DEBT_RATIO_GENERAL);

        // 기업의 부채비율 계산
        BigDecimal debtRatio = liabilities.divide(equity, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));

        // 부채 비율이 기준보다 높으면 20점 감점
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

    // 장단기 금리차 역전시 모든 기업에 감점
    private int calculateMacroPenalty(MacroEconomicData macro) {
        if(macro.getUs10yTreasuryYield() == null || macro.getUs2yTreasuryYield() == null) {
            log.error(
                    "장단기 금리차 계산 불가: 채권 데이터 누락 (10y: {}, 2y: {})",
                    macro.getUs10yTreasuryYield(),
                    macro.getUs2yTreasuryYield()
            );
            throw new BusinessException(ErrorCode.BOND_YIELD_NOT_FOUND);
        }

        if(macro.getUs10yTreasuryYield() < macro.getUs2yTreasuryYield()) {
            log.debug("페널티 적용: 장단기 금리차 역전");
            return PENALTY_SCORE_MACRO;
        }

        return 0;
    }

    // 고금리 상황에서 부채비율 높은 기업 감점 + 투자까지 확대할 경우 추가로 감점
    private int calculateRiskyInvestmentPenalty(FinancialStatement fs, MacroEconomicData macro) {
        if(macro.getUs10yTreasuryYield() == null) {
            log.error("고금리 판단 불가: 10년물 국채 금리 누락");
            throw new BusinessException(ErrorCode.BOND_YIELD_NOT_FOUND);
        }

        if(macro.getUs10yTreasuryYield() < HIGH_INTEREST_RATE_THRESHOLD) return 0;

        if(exceedsDebtRatio(fs, HIGH_DEBT_RATIO_GENERAL, HIGH_DEBT_RATIO_FINANCIAL)) {
            log.debug("페널티 적용: 고금리 상황의 고부채");
            if(isAggressiveInvestment(fs)) {
                log.debug("페널티 적용: 고금리 상황의 공격적 투자");
                return  PENALTY_SCORE_HiGH_DEBT_IN_HIGH_RATE + PENALTY_SCORE_RISKY_INVESTMENT;
            }
            return PENALTY_SCORE_HiGH_DEBT_IN_HIGH_RATE;
        }
        return 0;
    }

    // 부채 비율 판별
    private boolean exceedsDebtRatio(FinancialStatement fs, double generalLimit, double financialLimit) {
        BigDecimal equity = fs.getTotalEquity();

        if(equity == null) {
            log.error("부채 비율 계산 불가: 자본 데이터 누락 (기업: {})", fs.getCompany().getName());
            throw new BusinessException(ErrorCode.INVALID_FINANCIAL_DATA);
        }

        if(equity.compareTo(BigDecimal.ZERO) <= 0) {
            log.debug("부채 비율 계산 중 자본 잠식 확인(자본: {}) -> 기준 초과로 간주", equity);
            return true;
        }

        BigDecimal liabilities = fs.getTotalLiabilities();

        if(liabilities == null) {
            log.error("부채 비율 계산 불가: 부채 데이터 누락");
            throw new BusinessException(ErrorCode.INVALID_FINANCIAL_DATA);
        }
        BigDecimal debtRatio = liabilities.divide(equity, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        boolean isFinance = SECTOR_FINANCIAL.equalsIgnoreCase(fs.getCompany().getSector());
        double threshold = isFinance ? financialLimit : generalLimit;

        return debtRatio.doubleValue() > threshold;
    }

    // 위험한 투자 판별
    private boolean isAggressiveInvestment(FinancialStatement fs) {
        BigDecimal revenue = fs.getRevenue();
        if(revenue == null || revenue.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("위험 투자 분석 불가: 유효하지 않은 매출액 (기업: {}, 매출: {})",
                    fs.getCompany().getName(), revenue);
            throw new BusinessException(ErrorCode.INVALID_FINANCIAL_DATA);
        }

        // 연구개발비나 설비 투자가 없는 기업은 흔하므로 null을 예외가 아니라 0으로 처리
        BigDecimal invest = getValueOrDefault(fs.getResearchAndDevelopment())
                .add(getValueOrDefault(fs.getCapitalExpenditure()));

        double investRatio = invest.divide(revenue, 4, RoundingMode.HALF_UP).doubleValue() * 100;
        return investRatio >= AGGRESSIVE_INVESTMENT_RATIO;
    }

    // null 안전처리를 위한 헬퍼 메서드
    // null이면 0을 반환
    private BigDecimal getValueOrDefault(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

}

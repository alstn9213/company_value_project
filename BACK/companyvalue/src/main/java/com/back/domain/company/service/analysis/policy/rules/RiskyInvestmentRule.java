package com.back.domain.company.service.analysis.policy.rules;

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
public class RiskyInvestmentRule implements PenaltyRule {

  @Override
  public int apply(FinancialStatement fs, MacroEconomicData macro) {
    if (macro.getUs10yTreasuryYield() == null) {
      log.error("고금리 판단 불가: 10년물 국채 금리 누락");
      throw new BusinessException(ErrorCode.BOND_YIELD_NOT_FOUND);
    }

    if (macro.getUs10yTreasuryYield() < HIGH_INTEREST_RATE_THRESHOLD) return 0;

    if(exceedsDebtRatio(fs)) {
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
  private boolean exceedsDebtRatio(FinancialStatement fs) {
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
    double threshold = isFinance ? HIGH_DEBT_RATIO_FINANCIAL : HIGH_DEBT_RATIO_GENERAL;

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

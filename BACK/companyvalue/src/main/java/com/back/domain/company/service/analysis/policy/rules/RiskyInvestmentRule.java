package com.back.domain.company.service.analysis.policy.rules;

import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.service.analysis.constant.ScoringConstants;
import com.back.domain.macro.entity.MacroEconomicData;
import com.back.global.error.ErrorCode;
import com.back.global.error.exception.BusinessException;
import com.back.global.util.DecimalUtil;
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
    Double us10Y = macro.getUs10yTreasuryYield();

    if (us10Y == null) {
      throw new BusinessException(ErrorCode.BOND_YIELD_NOT_FOUND);
    }

    if (us10Y < HIGH_INTEREST_RATE_THRESHOLD) {
      return 0;
    }

    if (exceedsDebtRatio(fs)) {
      log.debug("[페널티] 고금리 상황의 고부채 기업 {}: ", fs.getCompany().getName());

      if (isAggressiveInvestment(fs)) {
        log.debug("[추가 페널티] 고부채 기업이 공격적 투자 감행");
        return PENALTY_SCORE_HiGH_DEBT_IN_HIGH_RATE + PENALTY_SCORE_RISKY_INVESTMENT;
      }
      return PENALTY_SCORE_HiGH_DEBT_IN_HIGH_RATE;
    }

    return 0;
  }

  // --- 헬퍼 메서드 ---

  // 부채 비율 판별 헬퍼
  private boolean exceedsDebtRatio(FinancialStatement fs) {
    String companyName = fs.getCompany().getName();
    BigDecimal equity = fs.getTotalEquity();
    BigDecimal liabilities = fs.getTotalLiabilities();

    if (equity == null || liabilities == null) {
      log.warn("[데이터 누락] {}: 자본={} 부채={}", companyName, equity, liabilities);
      throw new BusinessException(ErrorCode.INVALID_FINANCIAL_DATA);
    }

    if (equity.compareTo(BigDecimal.ZERO) <= 0) {
      log.debug("자본 잠식: {}, 자본: {} -> 부채 비율 기준 초과로 간주", companyName, equity);
      return true;
    }

    BigDecimal debtRatio = DecimalUtil.checkNullAndDivide(liabilities, equity, 4);

    boolean isFinance = fs.getCompany().isFinancialSector();
    double threshold = ScoringConstants.getDebtRatioLimit(isFinance).doubleValue();

    return debtRatio.doubleValue() > threshold;
  }

  // 위험한 투자 판별 헬퍼
  private boolean isAggressiveInvestment(FinancialStatement fs) {
    BigDecimal revenue = fs.getRevenue();

    // 매출액 유효성 검사
    if (revenue == null || revenue.compareTo(BigDecimal.ZERO) <= 0) {
      log.warn("위험 투자 분석 불가: 유효하지 않은 매출액 ({}) - 스킵 처리 (Company: {})",
              revenue, fs.getCompany().getName());
      throw new BusinessException(ErrorCode.INVALID_FINANCIAL_DATA);
    }

    // 연구개발비나 설비 투자가 없는 기업은 흔하므로 null을 예외가 아니라 0으로 처리
    BigDecimal invest = getValueOrDefault(fs.getResearchAndDevelopment())
            .add(getValueOrDefault(fs.getCapitalExpenditure()));

    double investRatio = DecimalUtil.checkNullAndDivide(invest, revenue, 4).doubleValue();
    return investRatio >= AGGRESSIVE_INVESTMENT_RATIO;
  }


  // null 안전처리를 위한 헬퍼
  private BigDecimal getValueOrDefault(BigDecimal value) {
    return value != null ? value : BigDecimal.ZERO;
  }

}

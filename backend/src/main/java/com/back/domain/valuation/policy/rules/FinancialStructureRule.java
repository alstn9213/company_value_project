package com.back.domain.company.service.analysis.policy.rules;

import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.service.analysis.constant.ScoringConstants;
import com.back.domain.macro.entity.MacroEconomicData;
import com.back.global.util.DecimalUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.back.domain.company.service.analysis.constant.ScoringConstants.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class FinancialStructureRule implements PenaltyRule {


  @Override
  public int apply(FinancialStatement fs, MacroEconomicData macro) {
    String companyName = fs.getCompany().getName();
    int penalty = 0;

    // 자본잠식 페널티 계산
    penalty += calculateCapitalImpairmentPenalty(fs, companyName);

    // 부채 페널티 계산
    penalty += calculateDebtRatioPenalty(fs, companyName);

    return penalty;
  }

  // --- 헬퍼 메서드 ---

  // [자본잠식] 페널티 계산 헬퍼
  private int calculateCapitalImpairmentPenalty(FinancialStatement fs, String companyName) {
    BigDecimal equity = fs.getTotalEquity();

    if (equity.compareTo(BigDecimal.ZERO) <= 0) {
      log.debug("[페널티] 자본 잠식: {}", companyName);
      return PENALTY_SCORE_CAPITAL_IMPAIRMENT;
    }

    return 0;
  }

  // [부채비율] 페널티 계산 헬퍼
  private int calculateDebtRatioPenalty(FinancialStatement fs, String companyName) {
    BigDecimal liabilities = fs.getTotalLiabilities();
    BigDecimal equity = fs.getTotalEquity();

    // 부채비율 = (부채 / 자본) * 100
    BigDecimal debtRatio = DecimalUtil.checkNullAndDivide(liabilities, equity, 4);

    // 업종별 부채 비율 기준 설정
    boolean isFinance = fs.getCompany().isFinancialSector();
    BigDecimal limitRatio = ScoringConstants.getDebtRatioLimit(isFinance);

    if (debtRatio.compareTo(limitRatio) > 0) {
      log.debug("[페널티] 과도한 부채비율: {}", companyName);
      return PENALTY_SCORE_EXCESSIVE_DEBT;
    }

    return 0;
  }

}

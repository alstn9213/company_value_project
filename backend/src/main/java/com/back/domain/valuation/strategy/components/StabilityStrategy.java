package com.back.domain.company.service.analysis.strategy.components;

import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.service.analysis.constant.ScoreCategory;
import com.back.domain.company.service.analysis.dto.ScoringDataDto;
import com.back.domain.company.service.analysis.policy.standard.StabilityStandard;
import com.back.global.util.DecimalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


@Slf4j
@Component
public class StabilityStrategy implements ScoringStrategy {

  @Override
  public int calculate(ScoringDataDto data) {
    FinancialStatement fs = data.fs();

    int debtScore = calculateDebtRatioScore(fs);
    int cashFlowScore = calculateCashFlowScore(fs);

    return debtScore + cashFlowScore;
  }

  @Override
  public ScoreCategory getCategory() {
    return ScoreCategory.STABILITY;
  }

  // --- 헬퍼 메서드 ---

  // [부채비율] 점수 계산 헬퍼
  private int calculateDebtRatioScore(FinancialStatement fs) {
    String companyName = fs.getCompany().getName();
    BigDecimal totalLiabilities = fs.getTotalLiabilities();
    BigDecimal totalEquity = fs.getTotalEquity();

    if (totalLiabilities == null || totalEquity == null) {
      log.warn("[데이터 누락] {}: 부채={}, 자본={}", companyName, totalLiabilities, totalEquity);
      return 0;
    }

    // 부채비율 계산: (부채 / 자본) * 100
    BigDecimal debtRatio = DecimalUtil.calculatePercentage(totalLiabilities, totalEquity);

    // 업종별 표준(Standard)을 사용하여 점수 계산
    if (fs.getCompany().isFinancialSector()) {
      return StabilityStandard.FinancialDebt.calculate(debtRatio);
    } else {
      return StabilityStandard.GeneralDebt.calculate(debtRatio);
    }
  }


  // [영업활동 현금흐름] 점수 계산 헬퍼
  private int calculateCashFlowScore(FinancialStatement fs) {
    String companyName = fs.getCompany().getName();
    BigDecimal operatingCashFlow = fs.getOperatingCashFlow();

    if (operatingCashFlow == null) {
      log.warn("[데이터 누락]: {}, 현금흐름 null", companyName);
      return 0;
    }

    // 흑자인 경우 점수 부여
    // 흑자인 경우 Standard에 정의된 PASS 점수 부여
    return operatingCashFlow.compareTo(BigDecimal.ZERO) > 0
            ? StabilityStandard.CASH_FLOW_PASS_SCORE
            : 0;
  }

}

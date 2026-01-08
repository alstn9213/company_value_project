package com.back.domain.company.service.analysis.strategy.components;

import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.service.analysis.constant.ScoreCategory;
import com.back.domain.company.service.analysis.dto.ScoringData;
import com.back.domain.company.service.analysis.policy.standard.StabilityStandard;
import com.back.global.util.DecimalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.back.domain.company.service.analysis.constant.ScoringConstants.SECTOR_FINANCIAL;

@Slf4j
@Component
public class StabilityStrategy implements ScoringStrategy {

  @Override
  public int calculate(ScoringData data) {
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
    BigDecimal totalLiabilities = fs.getTotalLiabilities();
    BigDecimal totalEquity = fs.getTotalEquity();

    // 자본 잠식 걸러내기
    if (totalEquity.compareTo(BigDecimal.ZERO) <= 0) {
      log.info("자본 잠식 상태로 인한 안정성 점수 0점 처리: {}", fs.getCompany().getName());
      return 0;
    }

    // 부채비율 계산: (부채 / 자본) * 100
    double debtRatio = DecimalUtil.divide(totalLiabilities, totalEquity, 4)
            .doubleValue() * 100;

    // 업종별 표준(Standard)을 사용하여 점수 계산
    if (isFinancialSector(fs.getCompany().getSector())) {
      return StabilityStandard.FinancialDebt.calculate(debtRatio);
    } else {
      return StabilityStandard.GeneralDebt.calculate(debtRatio);
    }
  }


  // [영업활동 현금흐름] 점수 계산 헬퍼
  private int calculateCashFlowScore(FinancialStatement fs) {
    BigDecimal operatingCashFlow = fs.getOperatingCashFlow();

    if (operatingCashFlow == null) {
      log.warn("영업활동 현금 흐름 데이터 누락: {}", fs.getCompany().getName());
      return 0;
    }

    // 흑자인 경우 점수 부여
    // 흑자인 경우 Standard에 정의된 PASS 점수 부여
    return operatingCashFlow.compareTo(BigDecimal.ZERO) > 0
            ? StabilityStandard.CASH_FLOW_PASS_SCORE
            : 0;
  }


  // --- 기본 헬퍼 메서드 ---

  // 금융업 여부 확인 헬퍼
  private boolean isFinancialSector(String sector) {
    return SECTOR_FINANCIAL.equalsIgnoreCase(sector);
  }





}

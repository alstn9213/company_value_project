package com.back.domain.company.service.analysis.strategy;

import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.service.analysis.dto.ScoringData;
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

  // --- 헬퍼 메서드 ---

  // 부채비율 점수 계산 헬퍼
  private int calculateDebtRatioScore(FinancialStatement fs) {
    BigDecimal totalLiabilities = fs.getTotalLiabilities();
    BigDecimal totalEquity = fs.getTotalEquity();

    // 데이터 유효성 검사 (Null 체크 및 자본 잠식 체크)
    if (totalLiabilities == null || totalEquity == null || totalEquity.compareTo(BigDecimal.ZERO) <= 0) {
      log.warn("부채, 자본 데이터가 누락됐거나 자본 잠식 상태입니다: {}", fs.getCompany().getName());
      return 0;
    }

    // 부채비율 계산: (부채 / 자본) * 100
    BigDecimal debtRatio = totalLiabilities.divide(totalEquity, 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100));

    // 업종별 평가 기준 분기
    if (isFinancialSector(fs.getCompany().getSector())) {
      return scoreFinancialSector(debtRatio);
    } else {
      return scoreGeneralSector(debtRatio);
    }
  }

  // 영업활동 현금흐름 점수 계산 헬퍼
  private int calculateCashFlowScore(FinancialStatement fs) {
    BigDecimal operatingCashFlow = fs.getOperatingCashFlow();

    if (operatingCashFlow == null) {
      log.warn("영업활동 현금 흐름 데이터 누락: {}", fs.getCompany().getName());
      return 0;
    }

    // 흑자인 경우 점수 부여
    return operatingCashFlow.compareTo(BigDecimal.ZERO) > 0 ? 20 : 0;
  }


  // --- 기본 헬퍼 메서드 ---

  // 금융업 여부 확인 헬퍼
  private boolean isFinancialSector(String sector) {
    return SECTOR_FINANCIAL.equalsIgnoreCase(sector);
  }

  // 금융업 부채비율 채점 (금융업은 부채비율이 높음)
  private int scoreFinancialSector(BigDecimal debtRatio) {
    if (debtRatio.compareTo(BigDecimal.valueOf(800)) < 0) return 20;
    else if (debtRatio.compareTo(BigDecimal.valueOf(1000)) < 0) return 10;
    else if (debtRatio.compareTo(BigDecimal.valueOf(1200)) < 0) return 5;
    return 0;
  }

  // 일반 기업 부채비율 채점
  private int scoreGeneralSector(BigDecimal debtRatio) {
    if (debtRatio.compareTo(BigDecimal.valueOf(100)) < 0) return 20;
    else if (debtRatio.compareTo(BigDecimal.valueOf(200)) < 0) return 10;
    else if (debtRatio.compareTo(BigDecimal.valueOf(300)) < 0) return 5;
    return 0;
  }



}

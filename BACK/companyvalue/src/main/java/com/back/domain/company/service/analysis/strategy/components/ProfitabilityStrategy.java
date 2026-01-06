package com.back.domain.company.service.analysis.strategy.components;

import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.service.analysis.constant.ScoreCategory;
import com.back.domain.company.service.analysis.dto.ScoringData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Component
public class ProfitabilityStrategy implements ScoringStrategy {

  @Override
  public int calculate(ScoringData data) {
    FinancialStatement fs = data.fs();

    int roeScore = calculateROEScore(fs);
    int opMarginScore = calculateOperatingMarginScore(fs);

    return roeScore + opMarginScore;
  }

  @Override
  public ScoreCategory getCategory() {
    return ScoreCategory.PROFITABILITY;
  }

  // --- 헬퍼 메서드 ---

  // ROE 점수 계산 헬퍼
  private int calculateROEScore(FinancialStatement fs) {
    BigDecimal netIncome = fs.getNetIncome();
    BigDecimal equity = fs.getTotalEquity();

    // 데이터 유효성 검사 (Null 체크 및 자본이 0 이하인 완전 자본 잠식 상태 체크)
    if (netIncome == null || equity == null || equity.compareTo(BigDecimal.ZERO) <= 0) {
      log.warn("순이익이나 자본 데이터가 누락됐거나 자본 잠식 상태입니다: {}", fs.getCompany().getName());
      return 0;
    }

    // ROE 계산: (당기순이익 / 자본총계) * 100
    double roe = netIncome.divide(equity, 4, RoundingMode.HALF_UP).doubleValue() * 100;

    // 점수 산정
    if (roe >= 20) return 15;
    else if (roe >= 10) return 10;
    else if (roe >= 0) return 5;

    return 0;
  }

  // 영업이익률 점수 계산 헬퍼
  private int calculateOperatingMarginScore(FinancialStatement fs) {
    BigDecimal revenue = fs.getRevenue();
    BigDecimal operatingProfit = fs.getOperatingProfit();

    // 데이터 유효성 검사 (Null 체크 및 매출액이 0인 경우 체크)
    // 매출액이 0이면 영업이익률 계산 불가 (0 나누기 방지)
    if (revenue == null || operatingProfit == null || revenue.compareTo(BigDecimal.ZERO) == 0) {
      log.warn("매출액이나 영업이익률 데이터가 누락됐거나 매출이 없는 기업(스타트업)입니다: {}", fs.getCompany().getName());
      return 0;
    }

    // 영업이익률 계산: (영업이익 / 매출액) * 100
    double opMargin = operatingProfit.divide(revenue, 4, RoundingMode.HALF_UP).doubleValue() * 100;

    // 점수 산정
    if (opMargin >= 20) return 15;
    else if (opMargin >= 10) return 10;
    else if (opMargin >= 0) return 5;

    return 0;
  }


}

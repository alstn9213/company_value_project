package com.back.domain.company.service.analysis;

import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.service.analysis.dto.MarketMetricsDto;
import com.back.global.util.DecimalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


@Slf4j
@Component
public class MarketMetricCalculator {
  public MarketMetricsDto calculate(FinancialStatement fs, BigDecimal stockPrice) {
    Long totalSharesObj = fs.getCompany().getTotalShares();

    // 주식 수가 없거나 0이면 계산 불가
    if (totalSharesObj == null || totalSharesObj == 0) {
      log.warn("[데이터 누락] 주식 수가 누락됐거나 0입니다.");
      return MarketMetricsDto.empty();
    }

    BigDecimal shares = BigDecimal.valueOf(totalSharesObj);
    BigDecimal per = BigDecimal.ZERO;
    BigDecimal pbr = BigDecimal.ZERO;

    // BPS (주당 순자산) = 자본총계 / 주식수
    BigDecimal bps = DecimalUtil.calculatePercentage(fs.getTotalEquity(), shares);

    // EPS (주당 순이익) = 순이익 / 주식수
    BigDecimal eps = DecimalUtil.calculatePercentage(fs.getNetIncome(), shares);

    // PER (주가수익비율) = 주가 / EPS
    if (eps.compareTo(BigDecimal.ZERO) > 0) {
      per = DecimalUtil.calculatePercentage(stockPrice, eps);
    }

    // PBR (주가순자산비율) = 주가 / BPS
    if (bps.compareTo(BigDecimal.ZERO) > 0) {
      pbr = DecimalUtil.calculatePercentage(stockPrice, bps);
    }

    return new MarketMetricsDto(eps, per, bps, pbr);
  }



}

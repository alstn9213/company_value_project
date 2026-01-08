package com.back.domain.company.service.analysis;

import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.service.analysis.dto.MarketMetrics;
import com.back.global.util.DecimalUtil;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;



@Component
public class MarketMetricCalculator {
  public MarketMetrics calculate(FinancialStatement fs, BigDecimal currentPrice) {
    Long totalSharesObj = fs.getCompany().getTotalShares();

    // 방어 로직: 주식 수가 없거나 0이면 계산 불가
    if (totalSharesObj == null || totalSharesObj == 0) {
      return MarketMetrics.empty();
    }

    BigDecimal shares = BigDecimal.valueOf(totalSharesObj);

    // 1. EPS (주당 순이익) = 순이익 / 주식수
    BigDecimal eps = DecimalUtil.divide(fs.getNetIncome(), shares, 2);

    // 2. PER (주가수익비율) = 주가 / EPS
    BigDecimal per = BigDecimal.ZERO;
    if (eps.compareTo(BigDecimal.ZERO) > 0) {
      per = DecimalUtil.divide(currentPrice, eps, 2);
    }

    // 3. BPS (주당 순자산) = 자본총계 / 주식수
    BigDecimal bps = DecimalUtil.divide(fs.getTotalEquity(), shares, 2);

    // 4. PBR (주가순자산비율) = 주가 / BPS
    BigDecimal pbr = BigDecimal.ZERO;
    if (bps.compareTo(BigDecimal.ZERO) > 0) {
      pbr = DecimalUtil.divide(currentPrice, bps, 2);
    }

    return new MarketMetrics(eps, per, bps, pbr);
  }



}

package com.back.domain.valuation.model;

import java.math.BigDecimal;

public record MarketMetricsDto(
        BigDecimal eps,
        BigDecimal per,
        BigDecimal bps,
        BigDecimal pbr
) {
  public static MarketMetricsDto empty() {
    return new MarketMetricsDto(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
  }
}

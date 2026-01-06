package com.back.domain.company.service.analysis.dto;

import java.math.BigDecimal;

public record MarketMetrics(
        BigDecimal eps,
        BigDecimal per,
        BigDecimal bps,
        BigDecimal pbr
) {
  public static MarketMetrics empty() {
    return new MarketMetrics(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
  }
}

package com.back.global.config.init.dto;

import com.back.domain.company.entity.Company;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CompanySeedDto(
        String ticker,
        String name,
        String sector,
        String exchange,
        Long totalShares,
        List<FinancialSeedDto> financials,
        List<StockSeedDto> stockHistory
) {
  public Company toEntity() {
    return Company.builder()
            .ticker(this.ticker())
            .name(this.name())
            .sector(this.sector())
            .exchange(this.exchange())
            .totalShares(this.totalShares())
            .build();
  }
}

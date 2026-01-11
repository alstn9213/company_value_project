package com.back.global.config.init.dto;

import com.back.domain.company.entity.Company;
import com.back.domain.company.entity.StockPriceHistory;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StockSeedDto(
        String date,
        BigDecimal closePrice
) {
  public StockPriceHistory toEntity(Company company) {
    return StockPriceHistory.builder()
            .company(company)
            .recordedDate(LocalDate.parse(this.date())) // 날짜 파싱 로직도 이곳에 응집
            .closePrice(this.closePrice())
            .build();
  }
}
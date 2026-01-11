package com.back.domain.company.dto.response;

import com.back.domain.company.entity.StockPriceHistory;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StockHistoryResponse(
        LocalDate date,
        BigDecimal close
) {
  public static StockHistoryResponse from(StockPriceHistory entity) {
    return new StockHistoryResponse(
            entity.getRecordedDate(),
            entity.getClosePrice()
    );
  }
}
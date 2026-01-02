package com.back.domain.company.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StockHistoryResponse(
        LocalDate date,
        BigDecimal close
) {

}
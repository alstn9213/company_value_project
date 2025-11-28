package com.companyvalue.companyvalue.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StockHistoryDto(
        LocalDate date,
        BigDecimal close
) {}
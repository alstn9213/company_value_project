package com.back.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StockHistoryDto(
        LocalDate date,
        BigDecimal close
) {}
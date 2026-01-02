package com.back.global.config.init.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StockSeedDto(
        String date,
        BigDecimal closePrice
) {}
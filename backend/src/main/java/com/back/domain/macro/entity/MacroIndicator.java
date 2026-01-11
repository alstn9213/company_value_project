package com.back.domain.macro.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MacroIndicator {
    US_10Y("DGS10", "10년물 국채 금리"),
    US_2Y("DGS2", "2년물 국채 금리"),
    FED_FUNDS("DFF", "기준 금리"),
    CPI("CPIAUCSL", "소비자 물가 지수"),
    UNEMPLOYMENT("UNRATE", "실업률");

    private final String seriesId;
    private final String description;
}

package com.back.domain.macro.dto;


import com.back.domain.macro.entity.MacroEconomicData;

import java.time.LocalDate;

public record MacroDataResponse(
        LocalDate date,
        Double fedFundsRate, // 기준금리
        Double us10y,        // 10년물 국채
        Double us2y,         // 2년물 국채
        Double spread,       // 장단기 금리차 (10y - 2y)
        Double inflation,    // 인플레이션
        Double unemployment  // 실업률
) {
    public static MacroDataResponse from(MacroEconomicData macro) {
        // null 방지 로직
        //
        double y10 = macro.getUs10yTreasuryYield() != null ? macro.getUs10yTreasuryYield() : 0.0;
        double y2 = macro.getUs2yTreasuryYield() != null ? macro.getUs2yTreasuryYield() : 0.0;

        return new MacroDataResponse(
                macro.getRecordedDate(),
                macro.getFedFundsRate(),
                y10,
                y2,
                Math.round((y10 - y2) * 100.0) / 100.0, // 소수점 2자리 반올림
                macro.getInflationRate(),
                macro.getUnemploymentRate()
        );
    }
}
package com.back.domain.macro.dto;


import com.back.domain.macro.entity.MacroEconomicData;

import java.time.LocalDate;
import java.util.Optional;

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
      double y10 = Optional.ofNullable(macro.getUs10yTreasuryYield()).orElse(0.0);
      double y2 = Optional.ofNullable(macro.getUs2yTreasuryYield()).orElse(0.0);

      double spread = Math.round((y10 - y2) * 100.0) / 100.0;

      return new MacroDataResponse(
              macro.getRecordedDate(),
              macro.getFedFundsRate(),
              y10,
              y2,
              spread,
              macro.getInflationRate(),
              macro.getUnemploymentRate()
      );
    }
}
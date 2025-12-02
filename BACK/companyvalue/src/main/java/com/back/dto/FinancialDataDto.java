package com.back.dto;

import com.fasterxml.jackson.databind.JsonNode;

public record FinancialDataDto(
        JsonNode incomeStatement,
        JsonNode balanceSheet,
        JsonNode cashFlow
) {

    // 3가지 데이터가 모두 정상적으로 존재하는지 확인하는 편의 메서드
    public boolean hasAllData() {
        return incomeStatement != null && balanceSheet != null && cashFlow != null;
    }
}

package com.companyvalue.companyvalue.controller;

import com.companyvalue.companyvalue.service.DataFetchService;
import com.companyvalue.companyvalue.service.MacroDataService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final DataFetchService dataFetchService;
    private final MacroDataService macroDataService;

    // 테스트 1: http://localhost:8080/test/company?symbol=AAPL
    @GetMapping("/test/company")
    public JsonNode testCompany(@RequestParam String symbol) {
        // INCOME_STATEMENT 호출 테스트
        return dataFetchService.getCompanyFinancials("INCOME_STATEMENT", symbol);
    }

    // 테스트 2: http://localhost:8080/test/macro?seriesId=DGS10
    @GetMapping("/test/macro")
    public JsonNode testMacro(@RequestParam String seriesId) {
        // 미 10년물 국채 금리 호출 테스트
        return dataFetchService.getMacroIndicator(seriesId);
    }

    @GetMapping("/test/macro/update")
    public String updateMacro() {
        macroDataService.updateMacroEconomicData();
        return "Macro Data Updated! Check DB.";
    }
}
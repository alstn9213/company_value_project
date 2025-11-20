package com.companyvalue.companyvalue.controller;

import com.companyvalue.companyvalue.domain.Company;
import com.companyvalue.companyvalue.domain.repository.CompanyRepository;
import com.companyvalue.companyvalue.dto.FinancialDataDto;
import com.companyvalue.companyvalue.service.DataFetchService;
import com.companyvalue.companyvalue.service.FinancialDataService;
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
    private final FinancialDataService financialDataService;
    private final CompanyRepository companyRepository;

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

    @GetMapping("/test/financial/update")
    public String updateFinancials(@RequestParam String ticker) {
        // 편의상 테스트할 때 기업이 없으면 자동 생성 (실제론 미리 있어야 함)
        if (companyRepository.findByTicker(ticker).isEmpty()) {
                companyRepository.save(Company.builder()
                .ticker(ticker).name(ticker).build());
        }

        // 1. 데이터 수집
        FinancialDataDto rawData = financialDataService.fetchRawFinancialData(ticker);
        // 2. 데이터 저장
        financialDataService.saveFinancialData(ticker, rawData);

        return ticker + " financials updated (Fetch -> Save 분리 완료)!";
    }
}
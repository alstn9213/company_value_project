package com.companyvalue.companyvalue.service;

import com.companyvalue.companyvalue.domain.Company;
import com.companyvalue.companyvalue.domain.FinancialStatement;
import com.companyvalue.companyvalue.domain.repository.CompanyRepository;
import com.companyvalue.companyvalue.domain.repository.FinancialStatementRepository;
import com.companyvalue.companyvalue.dto.FinancialDataDto;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinancialDataService {

    private final DataFetchService dataFetchService;
    private final CompanyRepository companyRepository;
    private final FinancialStatementRepository financialStatementRepository;

    /*
    * 외부 API 호출
    * DB 커넥션을 점유하지 않고 네트워크 통신만 수행한다. (Transaction 없음)
    * */
    public FinancialDataDto fetchRawFinancialData(String ticker) {
        log.info("재무 데이터 수집 시작(Network I/O: {}", ticker);

        JsonNode income = dataFetchService.getCompanyFinancials("INCOME_STATEMENT", ticker);
        JsonNode balance = dataFetchService.getCompanyFinancials("BALANCE_SHEET", ticker);
        JsonNode cash = dataFetchService.getCompanyFinancials("CASH_FLOW", ticker);

        return new FinancialDataDto(income, balance, cash);
    }

    /*
    * 데이터 파싱 및 DB 저장
    * 데이터가 준비된 상태에서 빠르게 DB 작업을 수행한다.(Transaction 있음)
    * */
    @Transactional
    public void saveFinancialData(String ticker, FinancialDataDto rawData) {
        log.info("재무 데이터 저장 시작(DB I/O): {}", ticker);
        if(!rawData.hasAllData()) {
            log.warn("재무 데이터 불충분으로 저장 건너뜀: {}", ticker);
            return;
        }

        // 1. 기업 정보 조회
        Company company = companyRepository.findByTicker(ticker)
                .orElseThrow(() -> new RuntimeException("회사를 찾을 수 없습니다.: " + ticker));

        // 2. 데이터 파싱 및 병합
        Map<String, FinancialDataMap> mergedData = new HashMap<>();

        processReports(rawData.incomeStatement().get("quarterlyReports"), mergedData, "INCOME");
        processReports(rawData.balanceSheet().get("quarterlyReports"), mergedData, "BALANCE");
        processReports(rawData.cashFlow().get("quarterlyReports"), mergedData, "CASH");

        // 3. Entity 변환 및 저장
        for (String date : mergedData.keySet()) {
            FinancialDataMap data = mergedData.get(date);
            if (data.hasAllData()) {
                saveFinancialStatement(company, date, data);
            }
        }
        log.info("재무 데이터 저장 완료: {}", ticker);
    }

    private void processReports(JsonNode reports, Map<String, FinancialDataMap> map, String type) {
        if (reports == null || !reports.isArray()) return;

        for (JsonNode report : reports) {
            String date = report.get("fiscalDateEnding").asText();
            map.putIfAbsent(date, new FinancialDataMap());
            FinancialDataMap dataMap = map.get(date);

            if ("INCOME".equals(type)) {
                dataMap.incomeNode = report;
            } else if ("BALANCE".equals(type)) {
                dataMap.balanceNode = report;
            } else if ("CASH".equals(type)) {
                dataMap.cashNode = report;
            }
        }
    }

    private void saveFinancialStatement(Company company, String dateStr, FinancialDataMap data) {
        LocalDate date = LocalDate.parse(dateStr);
        int year = date.getYear();
        int quarter = (date.getMonthValue() - 1) / 3 + 1; // 월 -> 분기 변환

        // 중복 저장 방지: 이미 해당 분기 데이터가 있으면 스킵하거나 업데이트 (여기선 스킵)
        boolean exists = financialStatementRepository
                .findTopByCompanyOrderByYearDescQuarterDesc(company)
                .filter(fs -> fs.getYear() == year && fs.getQuarter() == quarter)
                .isPresent();

        if (!exists) {
            FinancialStatement fs = FinancialStatement.builder()
                    .company(company)
                    .year(year)
                    .quarter(quarter)
                    // Income Statement
                    .revenue(parseBigDecimal(data.incomeNode, "totalRevenue"))
                    .operatingProfit(parseBigDecimal(data.incomeNode, "operatingIncome"))
                    .netIncome(parseBigDecimal(data.incomeNode, "netIncome"))
                    .researchAndDevelopment(parseBigDecimal(data.incomeNode, "researchAndDevelopment")) // R&D
                    // Balance Sheet
                    .totalAssets(parseBigDecimal(data.balanceNode, "totalAssets"))
                    .totalLiabilities(parseBigDecimal(data.balanceNode, "totalLiabilities"))
                    .totalEquity(parseBigDecimal(data.balanceNode, "totalShareholderEquity"))
                    // Cash Flow
                    .operatingCashFlow(parseBigDecimal(data.cashNode, "operatingCashflow"))
                    .capitalExpenditure(parseBigDecimal(data.cashNode, "capitalExpenditures")) // CapEx
                    .build();

            financialStatementRepository.save(fs);
        }
    }

    // JSON에서 숫자 안전하게 꺼내기 ("None"이나 null 처리)
    private BigDecimal parseBigDecimal(JsonNode node, String fieldName) {
        if (node == null || !node.has(fieldName)) return BigDecimal.ZERO;
        String value = node.get(fieldName).asText();
        if ("None".equalsIgnoreCase(value) || value == null || value.isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    // 데이터를 임시로 묶어두는 내부 클래스
    private static class FinancialDataMap {
        JsonNode incomeNode;
        JsonNode balanceNode;
        JsonNode cashNode;

        // 3가지 데이터가 다 존재하는지 확인
        boolean hasAllData() {
            return incomeNode != null && balanceNode != null && cashNode != null;
        }
    }
}
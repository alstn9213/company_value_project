package com.back.domain.company.service.finance;

import com.back.domain.company.entity.Company;
import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.repository.FinancialStatementRepository;
import com.back.infra.external.dto.ExternalFinancialDataResponse;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FinancialStatementService {

    private final FinancialStatementRepository financialStatementRepository;

    public List<FinancialStatement> getFinancialStatements(Company company) {
        return financialStatementRepository.findByCompanyOrderByYearDescQuarterDesc(company);
    }

    /**
     * 외부 API(Alpha Vantage)에서 가져온 3가지 재무 데이터를 병합하여 저장
     * (Income Statement, Balance Sheet, Cash Flow)
     */
    @Transactional
    public void saveFinancialStatements(Company company, ExternalFinancialDataResponse rawData) {
        // 데이터 병합 (날짜 기준)
        Map<String, FinancialDataMap> mergedData = new HashMap<>();

        processReports(rawData.incomeStatement().get("quarterlyReports"), mergedData, "INCOME");
        processReports(rawData.balanceSheet().get("quarterlyReports"), mergedData, "BALANCE");
        processReports(rawData.cashFlow().get("quarterlyReports"), mergedData, "CASH");

        // Entity 변환 및 저장
        int savedCount = 0;
        for(String dateStr : mergedData.keySet()) {
            FinancialDataMap data = mergedData.get(dateStr);

            // 3가지 데이터가 모두 존재해야 신뢰할 수 있는 재무제표로 간주
            if(data.hasAllData()) {
                boolean saved = saveFinancialStatementIfNew(company, dateStr, data);
                if (saved) savedCount++;
            }
        }

        if(savedCount > 0) {
            log.info("재무제표 저장 완료 - Ticker: {}, 건수: {}", company.getTicker(), savedCount);
        }
    }

    // --- 내부 메서드 ---

    private boolean saveFinancialStatementIfNew(Company company, String dateStr, FinancialDataMap data) {
        LocalDate date = LocalDate.parse(dateStr);
        int year = date.getYear();
        int quarter = (date.getMonthValue() - 1) / 3 + 1; // 월 -> 분기 변환 (예: 9월 -> 3분기)

        // 중복 방지: 이미 해당 연도/분기의 데이터가 있는지 확인
        boolean exists = financialStatementRepository
                .findTopByCompanyOrderByYearDescQuarterDesc(company)
                .filter(fs -> fs.getYear() == year && fs.getQuarter() == quarter)
                .isPresent();

        if(exists) return false;

        BigDecimal assets = parseBigDecimal(data.balanceNode, "totalAssets");
        BigDecimal liabilities = parseBigDecimal(data.balanceNode, "totalLiabilities");
        BigDecimal equity = parseBigDecimal(data.balanceNode, "totalShareholderEquity");

        // [중요 수정] 자본(Equity) 데이터가 누락되거나 0인 경우, (자산 - 부채) 공식으로 보정
        if (equity.compareTo(BigDecimal.ZERO) == 0 && assets.compareTo(BigDecimal.ZERO) > 0) {
            equity = assets.subtract(liabilities);
            log.debug("자본 데이터 보정됨 (Assets - Liabilities): {} -> {}", company.getTicker(), equity);
        }

        FinancialStatement fs = FinancialStatement.builder()
                .company(company)
                .year(year)
                .quarter(quarter)
                // Income Statement
                .revenue(parseBigDecimal(data.incomeNode, "totalRevenue"))
                .operatingProfit(parseBigDecimal(data.incomeNode, "operatingIncome"))
                .netIncome(parseBigDecimal(data.incomeNode, "netIncome"))
                .researchAndDevelopment(parseBigDecimal(data.incomeNode, "researchAndDevelopment"))
                // Balance Sheet
                .totalAssets(parseBigDecimal(data.balanceNode, "totalAssets"))
                .totalLiabilities(parseBigDecimal(data.balanceNode, "totalLiabilities"))
                .totalEquity(parseBigDecimal(data.balanceNode, "totalShareholderEquity"))
                // Cash Flow
                .operatingCashFlow(parseBigDecimal(data.cashNode, "operatingCashflow"))
                .capitalExpenditure(parseBigDecimal(data.cashNode, "capitalExpenditures"))
                .build();

        financialStatementRepository.save(fs);
        return true;
    }

    private void processReports(JsonNode reports, Map<String, FinancialDataMap> map, String type) {
        if(reports == null || !reports.isArray()) return;

        for(JsonNode report : reports) {
            if(!report.has("fiscalDateEnding")) continue;

            String date = report.get("fiscalDateEnding").asText();
            map.putIfAbsent(date, new FinancialDataMap());
            FinancialDataMap dataMap = map.get(date);

            if("INCOME".equals(type)) dataMap.incomeNode = report;
            else if("BALANCE".equals(type)) dataMap.balanceNode = report;
            else if("CASH".equals(type)) dataMap.cashNode = report;
        }
    }

    private BigDecimal parseBigDecimal(JsonNode node, String fieldName) {
        if(node == null || !node.has(fieldName)) return BigDecimal.ZERO;
        String value = node.get(fieldName).asText();

        if("None".equalsIgnoreCase(value) || value == null || value.isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    // 데이터를 임시로 묶어두는 내부 클래스 (DTO 역할)
    private static class FinancialDataMap {
        JsonNode incomeNode;
        JsonNode balanceNode;
        JsonNode cashNode;

        boolean hasAllData() {
            return incomeNode != null && balanceNode != null && cashNode != null;
        }
    }
}
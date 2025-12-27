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
        // --- 3개 영역의 데이터를 합쳐서 날짜별로 Map에 삽입 ---
        Map<String, FinancialDataMap> mergedData = new HashMap<>();
        processReports(rawData.incomeStatement().get("quarterlyReports"), mergedData, "INCOME");
        processReports(rawData.balanceSheet().get("quarterlyReports"), mergedData, "BALANCE");
        processReports(rawData.cashFlow().get("quarterlyReports"), mergedData, "CASH");

        // Entity 변환 및 저장
        for(String dateStr : mergedData.keySet()) {
            FinancialDataMap data = mergedData.get(dateStr);
            // 3가지 데이터가 모두 존재해야 신뢰할 수 있는 재무제표로 간주
            if(data.hasAllData()) saveFinancialStatementIfNew(company, dateStr, data);
        }

        log.info("재무제표 동기화 프로세스 완료 - Ticker: {}", company.getTicker());
    }

    // --- 헬퍼 메서드 ---
    // Map에 날짜별(키)로, 영역별(INCOME, BALANCE, CASH) 값을 삽입하는 헬퍼 메서드
    private void processReports(JsonNode apiReports, Map<String, FinancialDataMap> map, String type) {
        if(apiReports == null || !apiReports.isArray()) {
            log.warn("재무제표 데이터 형식이 올바르지 않거나 비어있습니다. Type: {}, Data: {}", type, apiReports);
            return;
        }

        // apiReports는 api로 불러온 재무 제표
        for(JsonNode apiReport : apiReports) {
            if(!apiReport.has("fiscalDateEnding")) continue;

            // 재무제표에서 날짜 추출
            String date = apiReport.get("fiscalDateEnding").asText();

            // map에 해당 날짜의 내부 클래스 객체가 비어있으면 새로 생성
            map.putIfAbsent(date, new FinancialDataMap());

            // 해당 날짜의 내부 클래스 객체를 가져온다.
            FinancialDataMap dataMap = map.get(date);

            // Type에 따라 객체에 재무제표의 데이터 삽입
            if("INCOME".equals(type)) dataMap.incomeNode = apiReport;
            else if("BALANCE".equals(type)) dataMap.balanceNode = apiReport;
            else if("CASH".equals(type)) dataMap.cashNode = apiReport;
        }
    }

    // 해당 분기의 날짜가 없다면 제무제표를 저장하는 헬퍼 메서드
    private void saveFinancialStatementIfNew(Company company, String dateStr, FinancialDataMap data) {
        LocalDate date = LocalDate.parse(dateStr);
        int year = date.getYear();
        int quarter = (date.getMonthValue() - 1) / 3 + 1; // 월 -> 분기 변환 (예: 9월 -> 3분기)

        // --- 중복 방지: 이미 해당 연도/분기의 데이터가 있는지 확인 ---
        boolean exists = financialStatementRepository
                .findTopByCompanyOrderByYearDescQuarterDesc(company)
                .filter(fs -> fs.getYear() == year && fs.getQuarter() == quarter)
                .isPresent();
        if(exists) return;

        BigDecimal assets = parseBigDecimal(data.balanceNode, "totalAssets");
        BigDecimal liabilities = parseBigDecimal(data.balanceNode, "totalLiabilities");
        BigDecimal equity = parseBigDecimal(data.balanceNode, "totalShareholderEquity");

        // --- 자본(Equity) 데이터가 누락되거나 0인 경우, (자산 - 부채) 공식으로 보정 ---
        // api로 재무제표를 가져올 때, 데이터가 없거나 파싱 실패 시 BigDecimal은 0을 반환한다.
        // 따라서 assets.compareTo(BigDecimal.ZERO) > 0 조건으로 정상적으로 자산값을 불러왔는지 확인이 필요하다.
        if(equity.compareTo(BigDecimal.ZERO) == 0 && assets.compareTo(BigDecimal.ZERO) > 0) {
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
    }

    //  재무제표의 특정 영역의 node 값을 BigDecimal 타입으로 파싱하는 헬퍼 메서드
    private BigDecimal parseBigDecimal(JsonNode node, String fieldName) {
        // 재무 제표에 특정 영역이 없는 것은 흔한 일이라
        // node가 null일때 에러 로그를 출력한다면 불필요한 로그가 쌓임
        // 따라서 아래의 조건에 예외처리를 하거나 로그를 남기지 않겠다.
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

    // --- 내부 클래스 ----
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
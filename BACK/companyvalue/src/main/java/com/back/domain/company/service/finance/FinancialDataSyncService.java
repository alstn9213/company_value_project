package com.back.domain.company.service.finance;

import com.back.domain.company.entity.Company;
import com.back.domain.company.repository.CompanyRepository;
import com.back.domain.company.service.stock.StockPriceService;
import com.back.infra.external.DataFetchService;
import com.back.infra.external.dto.ExternalFinancialDataResponse;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FinancialDataSyncService {

    private final CompanyRepository companyRepository;
    private final DataFetchService dataFetchService;
    private final FinancialStatementService  financialStatementService;
    private final StockPriceService stockPriceService;

    /**
     * 모든 회사의 재무 및 주가 데이터를 외부에서 가져와 저장합니다.
     * 스케줄러(SchedulingService)에 의해 주기적으로 호출됩니다.
     */
    @Transactional
    public void synchronizeAll() {
        log.info("모든 기업 데이터 동기화 시작");
        List<Company> companies = companyRepository.findAll();

        for (Company company : companies) {
            try {
                synchronizeCompany(company.getTicker());
            } catch (Exception e) {
                log.error("데이터 동기화 실패 - Ticker: {}, Error: {}", company.getTicker(), e.getMessage());
            }
        }
        log.info("모든 기업 데이터 동기화 완료");
    }

    /**
     * 특정 기업의 재무제표 및 주가 데이터를 동기화합니다.
     */
    @Transactional
    public void synchronizeCompany(String ticker) {
        log.info("기업 데이터 동기화 시작: {}", ticker);

        Company company = companyRepository.findByTicker(ticker)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 기업입니다: " + ticker));

        syncFinancialStatements(company);
        syncStockPrice(company);

        log.info("기업 데이터 동기화 완료: {}", ticker);
    }

    /*
     * 외부 API 호출
     * DB 커넥션을 점유하지 않고 네트워크 통신만 수행한다. (Transaction 없음)
     * */
    public ExternalFinancialDataResponse fetchRawFinancialData(String ticker) {
        log.info("재무 데이터 수집 시작(Network I/O: {}", ticker);

        JsonNode income = dataFetchService.getCompanyFinancials("INCOME_STATEMENT", ticker);
        JsonNode balance = dataFetchService.getCompanyFinancials("BALANCE_SHEET", ticker);
        JsonNode cash = dataFetchService.getCompanyFinancials("CASH_FLOW", ticker);

        return new ExternalFinancialDataResponse(income, balance, cash);
    }


    // --- 내부 헬퍼 메서드 ---

    private void syncFinancialStatements(Company company) {
        JsonNode income = dataFetchService.getCompanyFinancials("INCOME_STATEMENT", company.getTicker());
        JsonNode balance = dataFetchService.getCompanyFinancials("BALANCE_SHEET", company.getTicker());
        JsonNode cash = dataFetchService.getCompanyFinancials("CASH_FLOW", company.getTicker());

        ExternalFinancialDataResponse response = new ExternalFinancialDataResponse(income, balance, cash);

        // 데이터 유효성 검사 및 저장 위임 (DB I/O)
        if (response.hasAllData()) {
            financialStatementService.saveFinancialStatements(company, response);
        } else {
            log.warn("일부 재무제표 데이터가 누락되어 저장을 건너뜁니다. Ticker: {}", company.getTicker());
        }
    }

    private void syncStockPrice(Company company) {
        // 외부 API 호출 (Network I/O)
        JsonNode stockData = dataFetchService.getDailyStockHistory(company.getTicker());

        // 저장 위임 (DB I/O)
        if (stockData != null && !stockData.isEmpty()) {
            stockPriceService.saveStockPriceHistory(company, stockData);
        } else {
            log.warn("주가 데이터가 비어있습니다. Ticker: {}", company.getTicker());
        }
    }
}

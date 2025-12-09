package com.back.domain.company.service.finance;

import com.back.domain.company.entity.Company;
import com.back.domain.company.event.CompanyFinancialsUpdatedEvent;
import com.back.domain.company.repository.CompanyRepository;
import com.back.domain.company.service.stock.StockPriceService;
import com.back.infra.external.DataFetchService;
import com.back.infra.external.dto.ExternalFinancialDataResponse;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 모든 회사의 재무 및 주가 데이터를 외부에서 가져와 저장합니다.
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
     * 특정 기업의 재무제표 및 주가 데이터를 동기화하고, 완료 이벤트를 발행합니다.
     */
    @Transactional
    public void synchronizeCompany(String ticker) {
        log.info("기업 데이터 동기화 시작: {}", ticker);

        Company company = companyRepository.findByTicker(ticker)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 기업입니다: " + ticker));

        // 1. 재무제표 및 주가 데이터 수집 및 저장
        boolean fsSaved = syncFinancialStatements(company);
        boolean stockSaved = syncStockPrice(company);

        log.info("기업 데이터 저장 완료: {} (재무제표: {}, 주가: {})", ticker, fsSaved, stockSaved);

        // 2. 데이터 저장이 완료되었으므로 '점수 계산' 등을 수행하라고 이벤트를 발행
        eventPublisher.publishEvent(new CompanyFinancialsUpdatedEvent(ticker));
    }

    /*
     * 외부 API 호출
     * */
    public ExternalFinancialDataResponse fetchRawFinancialData(String ticker) {
        log.info("재무 데이터 수집 시작(Network I/O: {}", ticker);

        JsonNode income = dataFetchService.getCompanyFinancials("INCOME_STATEMENT", ticker);
        JsonNode balance = dataFetchService.getCompanyFinancials("BALANCE_SHEET", ticker);
        JsonNode cash = dataFetchService.getCompanyFinancials("CASH_FLOW", ticker);

        return new ExternalFinancialDataResponse(income, balance, cash);
    }


    // --- 내부 메서드 ---

    private boolean syncFinancialStatements(Company company) {
        try {
            JsonNode income = dataFetchService.getCompanyFinancials("INCOME_STATEMENT", company.getTicker());
            JsonNode balance = dataFetchService.getCompanyFinancials("BALANCE_SHEET", company.getTicker());
            JsonNode cash = dataFetchService.getCompanyFinancials("CASH_FLOW", company.getTicker());

            ExternalFinancialDataResponse response = new ExternalFinancialDataResponse(income, balance, cash);

            if (response.hasAllData()) {
                financialStatementService.saveFinancialStatements(company, response);
                return true;
            } else {
                log.warn("일부 재무제표 데이터 누락. Ticker: {}", company.getTicker());
                return false;
            }
        } catch (Exception e) {
            log.error("재무제표 동기화 중 오류: {}", e.getMessage());
            return false;
        }
    }

    private boolean syncStockPrice(Company company) {
        try {
            JsonNode stockData = dataFetchService.getDailyStockHistory(company.getTicker());
            if(stockData != null && !stockData.isEmpty()) {
                stockPriceService.saveStockPriceHistory(company, stockData);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("주가 데이터 동기화 중 오류: {}", e.getMessage());
            return false;
        }
    }
}

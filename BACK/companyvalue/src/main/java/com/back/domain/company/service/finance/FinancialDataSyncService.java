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

    // 특정 기업의 재무제표 및 주가 데이터를 동기화하고, 완료 이벤트를 발행하는 메서드
    @Transactional
    public void synchronizeCompany(String ticker) {
        log.info("기업 데이터 동기화 시작: {}", ticker);

        Company company = companyRepository.findByTicker(ticker)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 기업입니다: " + ticker));

        boolean fsSaved = syncFinancialStatements(company);
        boolean stockSaved = syncStockPrice(company);

        log.info("기업 데이터 저장 완료: {} (재무제표: {}, 주가: {})", ticker, fsSaved, stockSaved);

        // 데이터 저장이 완료되면, 점수 계산 이벤트 발행
        eventPublisher.publishEvent(new CompanyFinancialsUpdatedEvent(ticker));
    }


    // --- 내부 메서드 ---

    // 특정 기업의 재무 정보가 있는가 확인하는 내부 메서드
    private boolean syncFinancialStatements(Company company) {
        try {
            ExternalFinancialDataResponse response = fetchRawFinancialData(company.getTicker());

            if(response.hasAllData()) {
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

    // api로 특정 기업의 재무 정보를 가져오는 내부 메서드
    public ExternalFinancialDataResponse fetchRawFinancialData(String ticker) {
        log.info("재무 데이터 수집 시작(Network I/O: {}", ticker);

        JsonNode income = dataFetchService.getCompanyFinancials("INCOME_STATEMENT", ticker);
        JsonNode balance = dataFetchService.getCompanyFinancials("BALANCE_SHEET", ticker);
        JsonNode cash = dataFetchService.getCompanyFinancials("CASH_FLOW", ticker);

        return new ExternalFinancialDataResponse(income, balance, cash);
    }

    // 특정 기업의 주가 정보가 있는지 확인하는 내부 메서드
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

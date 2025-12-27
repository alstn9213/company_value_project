package com.back.domain.company.service.finance;

import com.back.domain.company.entity.Company;
import com.back.domain.company.event.CompanyFinancialsUpdatedEvent;
import com.back.domain.company.repository.CompanyRepository;
import com.back.domain.company.service.stock.StockPriceImportService;
import com.back.global.error.ErrorCode;
import com.back.global.error.exception.BusinessException;
import com.back.infra.external.DataFetchService;
import com.back.infra.external.dto.ExternalFinancialDataResponse;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FinancialDataSyncService {

    private final CompanyRepository companyRepository;
    private final DataFetchService dataFetchService;
    private final FinancialStatementService  financialStatementService;
    private final StockPriceImportService stockPriceService;
    private final ApplicationEventPublisher eventPublisher;

    // 특정 기업의 재무제표 및 주가 데이터를 동기화하고, 완료 이벤트를 발행하는 메서드
    @Transactional
    public void synchronizeCompany(String ticker) {
        log.info("기업 데이터 동기화 시작: {}", ticker);

        Company company = companyRepository.findByTicker(ticker)
                .orElseThrow(()-> new BusinessException(ErrorCode.COMPANY_NOT_FOUND));

        syncFinancialStatements(company);
        syncStockPrice(company);

        log.info("기업 데이터 저장 완료: {})", ticker);

        // 데이터 저장이 완료되면, 점수 계산 이벤트 발행
        eventPublisher.publishEvent(new CompanyFinancialsUpdatedEvent(ticker));
    }


    // --- 헬퍼 메서드 ---
    // 특정 기업의 재무 정보를 가져오고
    // 누락된 데이터가 있는지 확인한 후
    // DB에 저장하는 헬퍼 메서드
    private void syncFinancialStatements(Company company) {
        try {
            ExternalFinancialDataResponse response = dataFetchService.getCombinedFinancialData(company.getTicker());
            if (!response.hasAllData()) {
                log.warn("필수 재무제표 데이터가 누락되었습니다. Ticker: {}", company.getTicker());
                throw new BusinessException(ErrorCode.INVALID_FINANCIAL_DATA);
            }
            financialStatementService.saveFinancialStatements(company, response);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("재무제표 동기화 중 알 수 없는 오류 발생: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


    // 특정 기업의 주가 정보를 가져오고 누락된 데이터가 있는지 확인한 후 DB에 저장하는 헬퍼 메서드
    private void syncStockPrice(Company company) {
        try {
            JsonNode stockData = dataFetchService.getDailyStockHistory(company.getTicker());
            if (stockData == null || stockData.isEmpty()) {
                log.warn("주가 데이터가 존재하지 않습니다. Ticker: {}", company.getTicker());
                throw new BusinessException(ErrorCode.LATEST_STOCK_NOT_FOUND);
            }
            stockPriceService.fetchAndSaveStockHistory(company, stockData);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("주가 데이터 동기화 중 오류 알 수 없는 오류 발생: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


}

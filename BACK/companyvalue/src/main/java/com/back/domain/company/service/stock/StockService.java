package com.back.domain.company.service.stock;

import com.back.domain.company.entity.Company;
import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.entity.StockPriceHistory;
import com.back.domain.company.repository.CompanyRepository;
import com.back.domain.company.repository.FinancialStatementRepository;
import com.back.domain.company.repository.StockPriceHistoryRepository;
import com.back.domain.company.dto.response.StockHistoryResponse;
import com.back.global.config.init.DummyDataGenerator;
import com.back.global.error.ErrorCode;
import com.back.global.error.exception.BusinessException;
import com.back.infra.external.DataFetchService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {

    private final StockPriceHistoryRepository stockRepository;
    private final StockPriceImportService stockPriceImportService;
    private final CompanyRepository companyRepository;
    private final FinancialStatementRepository financialStatementRepository;
    private final DataFetchService dataFetchService;
    private final DummyDataGenerator dummyDataGenerator;

    private static final String REAL_DATA_TICKER = "AAPL";

    // DB에서 주가 데이터를 가져오는 메서드.
    // 주가 차트 그리기 용
    @Transactional
    @Cacheable(value = "stock_history", key = "#ticker", unless = "#result == null || #result.isEmpty()")
    public List<StockHistoryResponse> getStockHistory(String ticker) {
        Company company = companyRepository.findByTicker(ticker)
                .orElseThrow(()-> new BusinessException(ErrorCode.COMPANY_NOT_FOUND));
        List<StockPriceHistory> histories = stockRepository.findByCompanyOrderByRecordedDateAsc(company);

        // 주가 데이터가 없거나 최신화 안 돼있으면 최신화
        if(histories.isEmpty() || isOutdated(histories)) {
            if(isRealCompany(ticker)) {
                log.info("[StockService] 외부 API에서 데이터 갱신 시도: {}", ticker);
                JsonNode json = dataFetchService.getDailyStockHistory(ticker);
                histories = stockPriceImportService.fetchAndSaveStockHistory(company, json);
            } else {
                log.info("[StockService] 더미 데이터 갱신 시도: {}", ticker);
                histories = regenerateDummyData(company);
            }
        }

        // Entity -> DTO 변환하여 반환 (이 결과가 Redis에 저장됨)
        return histories.stream()
                .map(h -> new StockHistoryResponse(h.getRecordedDate(), h.getClosePrice()))
                .toList();
    }

    // 최신 데이터인지 확인하는 헬퍼 메서드
    private boolean isOutdated(List<StockPriceHistory> histories) {
        StockPriceHistory latestHistory = histories.get(histories.size() - 1);
        LocalDate latestDate = latestHistory.getRecordedDate();

        // 최신 데이터가 어제보다 이전이라면 업데이트 필요
        return latestDate.isBefore(LocalDate.now().minusDays(1));
    }

    // 더미 회사인지 확인하는 헬퍼 메서드
    private boolean isRealCompany(String ticker) {
        return REAL_DATA_TICKER.equalsIgnoreCase(ticker);
    }

    // 더미 데이터 재생성 헬퍼 메서드
    private List<StockPriceHistory> regenerateDummyData(Company company) {
        // 기존 더미 데이터 삭제 (중복 방지 및 최신 날짜 기준 재계산)
        stockRepository.deleteByCompany(company);

        // 재무제표 가져오기 (주가 생성 알고리즘에 필요)
        List<FinancialStatement> financials = financialStatementRepository.findByCompanyOrderByYearDescQuarterDesc(company);

        // 만약 재무제표도 없다면 생성 (안전장치)
        if(financials.isEmpty()) {
            financials = dummyDataGenerator.generateFinancials(company);
            financialStatementRepository.saveAll(financials);
        }

        // 새 더미 주가 생성 및 저장
        List<StockPriceHistory> newHistories = dummyDataGenerator.generateStockPrices(company, financials);
        return stockRepository.saveAll(newHistories);
    }

}

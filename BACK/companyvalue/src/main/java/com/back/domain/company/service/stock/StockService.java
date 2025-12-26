package com.back.domain.company.service.stock;

import com.back.domain.company.entity.Company;
import com.back.domain.company.entity.StockPriceHistory;
import com.back.domain.company.repository.CompanyRepository;
import com.back.domain.company.repository.StockPriceHistoryRepository;
import com.back.domain.company.dto.response.StockHistoryResponse;
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
    private final DataFetchService dataFetchService;

    // DB에서 주가 데이터를 가져오는 메서드.
    // 특정 기업의 주가 차트용
    @Transactional
    @Cacheable(value = "stock_history", key = "#ticker", unless = "#result == null || #result.isEmpty()")
    public List<StockHistoryResponse> getStockHistory(String ticker) {
        Company company = companyRepository.findByTicker(ticker)
                .orElseThrow(()-> new BusinessException(ErrorCode.COMPANY_NOT_FOUND));

        List<StockPriceHistory> histories = stockRepository.findByCompanyOrderByRecordedDateAsc(company);

        if(histories.isEmpty() || isOutdated(histories)) {
            JsonNode json = dataFetchService.getDailyStockHistory(ticker);
            histories = stockPriceImportService.fetchAndSaveStockHistory(company, json);
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

}

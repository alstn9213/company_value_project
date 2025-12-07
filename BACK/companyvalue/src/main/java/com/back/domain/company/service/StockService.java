package com.back.domain.company.service;

import com.back.domain.company.entity.Company;
import com.back.domain.company.entity.StockPriceHistory;
import com.back.domain.company.repository.CompanyRepository;
import com.back.domain.company.repository.StockPriceHistoryRepository;
import com.back.domain.company.dto.response.StockHistoryResponse;
import com.back.infra.external.DataFetchService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {

    private final StockPriceHistoryRepository stockRepository;
    private final CompanyRepository companyRepository;
    private final DataFetchService dataFetchService;

    @Transactional
    @Cacheable(value = "stock_history", key = "#ticker", unless = "#result == null || #result.isEmpty()")
    public List<StockHistoryResponse> getStockHistory(String ticker) {
        Company company = companyRepository.findByTicker(ticker)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 기업입니다."));
        List<StockPriceHistory> histories = stockRepository.findByCompanyOrderByRecordedDateAsc(company);
        if (histories.isEmpty()) histories = fetchAndSaveStockHistory(company);

        // Entity -> DTO 변환하여 반환 (이 결과가 Redis에 저장됨)
        return histories.stream()
                .map(h -> new StockHistoryResponse(h.getRecordedDate(), h.getClosePrice()))
                .toList();
    }

    private List<StockPriceHistory> fetchAndSaveStockHistory(Company company) {
        try {
            JsonNode json = dataFetchService.getDailyStockHistory(company.getTicker());
            JsonNode timeSeries = json.path("Time Series (Daily)");
            if(timeSeries.isMissingNode()) {
                log.warn("API 호출 실패 또는 제한 도달: {}", company.getTicker());
                return Collections.emptyList();
            }
            List<StockPriceHistory> newHistories = new ArrayList<>();
            Iterator<String> dates = timeSeries.fieldNames();

            int limit = 365; // 최근 1년(약 250~300 거래일) 데이터만 저장
            int count = 0;
            while (dates.hasNext() && count < limit) {
                String dateStr = dates.next();
                JsonNode dayData = timeSeries.get(dateStr);

                LocalDate date = LocalDate.parse(dateStr);
                BigDecimal close = new BigDecimal(dayData.path("4. close").asText());

                // DB에 저장
                StockPriceHistory history = StockPriceHistory.builder()
                        .company(company)
                        .recordedDate(date)
                        .closePrice(close)
                        .build();

                newHistories.add(history);
                count++;
            }

            Collections.reverse(newHistories); // 날짜 오름차순 정렬 (API는 내림차순으로 줌)

            return stockRepository.saveAll(newHistories);

        } catch (Exception e) {
            log.error("주가 데이터 처리 중 오류: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}

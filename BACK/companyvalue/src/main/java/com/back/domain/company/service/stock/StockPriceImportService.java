package com.back.domain.company.service.stock;

import com.back.domain.company.entity.Company;
import com.back.domain.company.entity.StockPriceHistory;
import com.back.domain.company.repository.StockPriceHistoryRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockPriceImportService {

    private final StockPriceHistoryRepository stockPriceHistoryRepository;

    /**
     * 외부 API(Alpha Vantage)에서 가져온 주가 데이터를 파싱하여 DB에 저장
     * Alpha Vantage "Time Series (Daily)" 포맷 대응
     */
    @Transactional
    public List<StockPriceHistory> fetchAndSaveStockHistory(Company company, JsonNode stockDataNode) {
        if(stockDataNode == null || !stockDataNode.has("Time Series (Daily)")) {
            log.warn("주가 데이터 형식이 올바르지 않거나 데이터가 없습니다. Ticker: {}", company.getTicker());
            return Collections.emptyList();
        }

        // --- 날짜별 주가 가져오기 ---
        JsonNode timeSeries = stockDataNode.get("Time Series (Daily)");
        Iterator<String> dates = timeSeries.fieldNames(); // 날짜별 데이터 순회 (최신 날짜부터 들어옴)

        // --- 중복 방지를 위해 DB에 저장된 최신 주가의 날짜 정보 가져와서, 그 날짜 이후의 주가만 가져오기 ---
        StockPriceHistory latestStock = stockPriceHistoryRepository.findTopByCompanyOrderByRecordedDateDesc(company);
        LocalDate latestSavedDate = (latestStock != null)
                ? latestStock.getRecordedDate()
                : LocalDate.MIN;
        List<StockPriceHistory> newHistoryList = new ArrayList<>();

        int limit = 365; // 1년치 주가 데이터
        int count = 0;

        while(dates.hasNext() && count < limit) {
            String dateStr = dates.next();
            LocalDate recordedDate = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            JsonNode dailyData = timeSeries.get(dateStr);

            // DB에 저장된 날짜가 최신 날짜면 중단
            if(!recordedDate.isAfter(latestSavedDate)) break;

            // 필드에서 가져온 종가를 BigDecimal로 파싱
            BigDecimal closePrice = parseBigDecimal(dailyData, "4. close");

            StockPriceHistory history = StockPriceHistory.builder()
                    .company(company)
                    .recordedDate(recordedDate)
                    .closePrice(closePrice)
                    .build();

            newHistoryList.add(history);
            count++;
        }

        // DB에 저장
        if(!newHistoryList.isEmpty()) {
            Collections.reverse(newHistoryList);
            return stockPriceHistoryRepository.saveAll(newHistoryList);
        } else {
            return Collections.emptyList();
        }
    }


    // --- 내부 메서드 ---

    // JSON 노드에서 BigDecimal 추출하는 내부 메서드
    private BigDecimal parseBigDecimal(JsonNode node, String fieldName) {
        if(node == null || !node.has(fieldName)) return BigDecimal.ZERO;
        String value = node.get(fieldName).asText();
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            log.warn("숫자 변환 오류: {}", value);
            return BigDecimal.ZERO;
        }
    }


}

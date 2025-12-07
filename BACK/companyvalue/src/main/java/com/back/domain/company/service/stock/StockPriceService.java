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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockPriceService {

    private final StockPriceHistoryRepository stockPriceHistoryRepository;

    // 특정 기업의 주가 기록 전체 조회 (차트용)
    public List<StockPriceHistory> getStockPriceHistory(Company company) {
        return stockPriceHistoryRepository.findByCompanyOrderByRecordedDateAsc(company);
    }

    /**
     * 외부 API(Alpha Vantage)에서 가져온 주가 데이터를 파싱하여 DB에 저장
     * Alpha Vantage "Time Series (Daily)" 포맷 대응
     */
    @Transactional
    public void saveStockPriceHistory(Company company, JsonNode stockDataNode) {
        // 1. 데이터 유효성 검증
        if (stockDataNode == null || !stockDataNode.has("Time Series (Daily)")) {
            log.warn("주가 데이터 형식이 올바르지 않거나 데이터가 없습니다. Ticker: {}", company.getTicker());
            return;
        }

        JsonNode timeSeries = stockDataNode.get("Time Series (Daily)");
        List<StockPriceHistory> newHistoryList = new ArrayList<>();

        // 2. 날짜별 데이터 순회 (최신 날짜부터 들어옴)
        Iterator<Map.Entry<String, JsonNode>> fields = timeSeries.fields();

        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String dateStr = entry.getKey();
            JsonNode dailyData = entry.getValue();

            LocalDate recordedDate = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);

            // 3. 중복 확인 (이미 저장된 날짜는 건너뜀)
            // 대량 데이터의 경우 성능 이슈가 있을 수 있으므로, 실제 운영 시에는
            // '최신 저장 날짜'를 조회해서 그 이후 데이터만 필터링하는 방식을 권장합니다.
            if (stockPriceHistoryRepository.existsByCompanyAndRecordedDate(company, recordedDate)) {
                continue;
            }

            // 4. 데이터 파싱 ("4. close" 필드 사용)
            BigDecimal closePrice = parseBigDecimal(dailyData, "4. close");

            StockPriceHistory history = StockPriceHistory.builder()
                    .company(company)
                    .recordedDate(recordedDate)
                    .closePrice(closePrice)
                    .build();

            newHistoryList.add(history);
        }

        // 5. 일괄 저장
        if (!newHistoryList.isEmpty()) {
            stockPriceHistoryRepository.saveAll(newHistoryList);
            log.info("주가 데이터 저장 완료 - Ticker: {}, 건수: {}", company.getTicker(), newHistoryList.size());
        } else {
            log.info("저장할 새로운 주가 데이터가 없습니다. Ticker: {}", company.getTicker());
        }
    }

    /**
     * JSON 노드에서 안전하게 BigDecimal 추출
     */
    private BigDecimal parseBigDecimal(JsonNode node, String fieldName) {
        if (node == null || !node.has(fieldName)) return BigDecimal.ZERO;
        String value = node.get(fieldName).asText();
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            log.warn("숫자 변환 오류: {}", value);
            return BigDecimal.ZERO;
        }
    }


}

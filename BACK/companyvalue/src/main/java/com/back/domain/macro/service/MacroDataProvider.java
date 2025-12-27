package com.back.domain.macro.service;

import com.back.domain.macro.entity.MacroIndicator;
import com.back.infra.external.DataFetchService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MacroDataProvider {
    private final DataFetchService dataFetchService;

    // 거시경제 특정 지표의 과거 데이터를 전부 가져와 날짜별 Map으로 반환
    public Map<LocalDate, Double> fetchHistory(MacroIndicator indicator) {
        Map<LocalDate, Double> historyData = new HashMap<>();

        try {
            JsonNode rootNode = dataFetchService.getMacroIndicator(indicator.getSeriesId());
            JsonNode observations = rootNode.get("observations");

            if(observations != null && observations.isArray()) {
                for(JsonNode node : observations) {
                    String valueStr = node.get("value").asText();
                    if(".".equals(valueStr)) continue; // "."은 데이터 없음

                    LocalDate date = LocalDate.parse(node.get("date").asText());
                    Double value = Double.parseDouble(valueStr);

                    historyData.put(date, value);
                }
            }
        } catch (Exception e) {
            log.error("과거 기록 수집 중 오류 발생 {}: {}", indicator.getSeriesId(), e.getMessage());
        }

        return historyData;
    }

    // 특정 지표의 가장 최신 유효 값을 반환
    public Double fetchLatestValue(MacroIndicator indicator) {
        try {
            JsonNode rootNode = dataFetchService.getMacroIndicator(indicator.getSeriesId());
            JsonNode observations = rootNode.get("observations");

            if (observations != null && observations.isArray()) {
                // 배열의 끝(최신 데이터)부터 역순으로 탐색
                for (int i = observations.size() - 1; i >= 0; i--) {
                    JsonNode node = observations.get(i);
                    String valueStr = node.get("value").asText();

                    if (!".".equals(valueStr)) {
                        double value = Double.parseDouble(valueStr);
                        log.debug("Fetched {} : {}", indicator.name(), value);
                        return value;
                    }
                }
            }
        } catch (Exception e) {
            log.error("최신 데이터 수집 중 오류 {}: {}", indicator.getSeriesId(), e.getMessage());
        }
        return null;
    }
}
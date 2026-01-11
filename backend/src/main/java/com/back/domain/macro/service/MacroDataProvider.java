package com.back.domain.macro.service;

import com.back.domain.macro.dto.FredApiResponse;
import com.back.domain.macro.entity.MacroIndicator;
import com.back.infra.client.fred.DataFetchService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MacroDataProvider {
  private final DataFetchService dataFetchService;
  private final ObjectMapper objectMapper;

  // 거시경제 특정 지표의 과거 데이터를 전부 가져와 날짜별 Map으로 반환
  public Map<LocalDate, Double> fetchHistory(MacroIndicator indicator) {
    Map<LocalDate, Double> historyData = new HashMap<>();

    try {
      JsonNode rootNode = dataFetchService.getMacroIndicator(indicator.getSeriesId());
      FredApiResponse response = objectMapper.treeToValue(rootNode, FredApiResponse.class);

      if (response.observations() == null) return historyData;

      for (FredApiResponse.FredObservation obs : response.observations()) {
        if (".".equals(obs.value())) continue; // "."은 데이터 없음

        LocalDate date = LocalDate.parse(obs.date());
        Double value = Double.parseDouble(obs.value());

        historyData.put(date, value);
      }
    } catch (Exception e) {
      log.error("거시 경제 과거 기록 수집 중 오류 발생 {}: {}", indicator.getSeriesId(), e.getMessage());
    }
    return historyData;
  }

  // 특정 지표의 가장 최신 유효 값을 반환
  public Optional<Double> fetchLatestValue(MacroIndicator indicator) {
    try {
      JsonNode rootNode = dataFetchService.getMacroIndicator(indicator.getSeriesId());
      FredApiResponse response = objectMapper.treeToValue(rootNode, FredApiResponse.class);

      List<FredApiResponse.FredObservation> observations = response.observations();

      if (observations != null && !observations.isEmpty()) {
        // 배열의 끝(최신 데이터)부터 역순으로 탐색
        for (int i = observations.size() - 1; i >= 0; i--) {
          FredApiResponse.FredObservation obs = observations.get(i);
          if (!".".equals(obs.value())) return Optional.of(Double.parseDouble(obs.value()));
        }
      }

    } catch (Exception e) {
      log.error("거시 경제 최신 데이터 조회 실패 {}: {}", indicator.getSeriesId(), e.getMessage());
    }
    return Optional.empty();
  }
}
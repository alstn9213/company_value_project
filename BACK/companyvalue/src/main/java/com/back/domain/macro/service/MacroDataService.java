package com.back.domain.macro.service;

import com.back.domain.macro.dto.MacroDataResponse;
import com.back.domain.macro.entity.MacroEconomicData;
import com.back.domain.macro.entity.MacroIndicator;
import com.back.domain.macro.repository.MacroRepository;
import com.back.infra.external.DataFetchService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MacroDataService {

    private final MacroRepository macroRepository;
    private final DataFetchService dataFetchService;

    // 과거 데이터 일괄 초기화 메서드
    @Transactional
    public void initHistoricalMacroData() {
        log.info("거시 경제 과거 데이터 수집 시작");

        // 날짜별 지표 값을 담을 임시 저장소
        Map<LocalDate, Map<MacroIndicator, Double>> historyMap = new HashMap<>();

        for(MacroIndicator indicator : MacroIndicator.values()) {
            collectHistory(historyMap, indicator);
        }

        // 날짜 정렬 (빈 데이터 채우기를 위해 순서가 중요함)
        List<LocalDate> sortedDates = historyMap.keySet().stream()
                .sorted()
                .toList();

        //  Map을 Entity로 변환하여 저장
        List<MacroEconomicData> dataList = new ArrayList<>();

        // 직전 유효 값을 기억할 변수들
        Double lastCpi = null;
        Double lastUnemployment = null;

        for(LocalDate date : sortedDates) {
            Map<MacroIndicator, Double> values = historyMap.get(date);

            // 현재 날짜에 값이 있으면 갱신, 없으면 직전 값(lastCpi) 사용
            Double currentCpi = values.get(MacroIndicator.CPI);
            if(currentCpi != null) lastCpi = currentCpi;

            Double currentUnemployment = values.get(MacroIndicator.UNEMPLOYMENT);
            if(currentUnemployment != null) lastUnemployment = currentUnemployment;

            // 이미 해당 날짜 데이터가 DB에 있는지 확인 (중복 방지)
            MacroEconomicData macroData = macroRepository.findByRecordedDate(date)
                    .orElseGet(() -> MacroEconomicData.builder()
                            .recordedDate(date)
                            .build());

            // getOrDefault를 사용하여 null을 안전하게 처리
            macroData.updateData(
                    values.getOrDefault(MacroIndicator.FED_FUNDS, macroData.getFedFundsRate()),
                    values.getOrDefault(MacroIndicator.US_10Y, macroData.getUs10yTreasuryYield()),
                    values.getOrDefault(MacroIndicator.US_2Y, macroData.getUs2yTreasuryYield()),
                    lastCpi,
                    lastUnemployment
            );

            dataList.add(macroData);
        }

        macroRepository.saveAll(dataList);
        log.info("거시 경제 과거 데이터 초기화 완료: 총 {}건", dataList.size());
    }

    /**
     * 매일 최신 데이터 업데이트 (스케줄러 호출)
     */
    @Transactional
    public void updateMacroEconomicData() {
        log.info("거시 경제 정보 업데이트 시작...");

        Map<MacroIndicator, Double> latestValues = new EnumMap<>(MacroIndicator.class);
        for(MacroIndicator indicator : MacroIndicator.values()) {
            latestValues.put(indicator, fetchLatestValue(indicator));
        }

        // 결측치 보정: 값이 없으면 가장 최근(어제) 데이터를 가져와서 채움
        MacroEconomicData lastData = macroRepository.findTopByOrderByRecordedDateDesc().orElse(null);
        if(lastData != null) {
            fillMissingMonthlyData(latestValues, MacroIndicator.CPI, lastData.getInflationRate());
            fillMissingMonthlyData(latestValues, MacroIndicator.UNEMPLOYMENT, lastData.getUnemploymentRate());
        }

        LocalDate today = LocalDate.now();

        // DB에 저장하기 (오늘 날짜 데이터가 이미 있으면 업데이트, 없으면 생성)
        MacroEconomicData macroData = macroRepository.findByRecordedDate(today)
                .orElseGet(() -> MacroEconomicData.builder()
                        .recordedDate(today)
                        .build());

        macroData.updateData(
                latestValues.get(MacroIndicator.FED_FUNDS),
                latestValues.get(MacroIndicator.US_10Y),
                latestValues.get(MacroIndicator.US_2Y),
                latestValues.get(MacroIndicator.CPI),
                latestValues.get(MacroIndicator.UNEMPLOYMENT)
        );

        macroRepository.save(macroData);

        log.info("거시 경제 정보 최신화 완료: {}", today);
    }

    // 최신 지표 조회 (DTO만 캐싱)
    @Cacheable(value = "macro_latest", key = "'latest'", unless = "#result == null")
    public MacroDataResponse getLatestData() {
        return macroRepository.findTopByOrderByRecordedDateDesc()
                .map(MacroDataResponse::from)
                .orElse(null);
    }

    // 과거 데이터 조회 (DTO 리스트만 캐싱)
    @Cacheable(value = "macro_history", key = "'history'", unless = "#result == null")
    public List<MacroDataResponse> getHistoryData() {
        return macroRepository.findTop3650ByOrderByRecordedDateDesc()
                .stream()
                .map(MacroDataResponse::from)
                .sorted(Comparator.comparing(MacroDataResponse::date))
                .toList();
    }

    // --- Helper Methods ---

    // 값이 없으면 이전 데이터로 채우는 로직 분리
    private void fillMissingMonthlyData(Map<MacroIndicator, Double> values, MacroIndicator indicator, Double lastValue) {
        if(values.get(indicator) == null && lastValue != null) {
            values.put(indicator, lastValue);
            log.info("오늘 {} 데이터 없음 -> 직전 값으로 대체: {}", indicator.getDescription(), lastValue);
        }
    }

    /**
     * API 응답 JSON에서 가장 최신의 유효한 값을 추출하는 헬퍼 메서드
     */
    private Double fetchLatestValue(MacroIndicator indicator) {
        try{
            JsonNode rootNode = dataFetchService.getMacroIndicator(indicator.getSeriesId());
            JsonNode observations = rootNode.get("observations");

            if(observations != null && observations.isArray()) {
                // 배열의 끝(최신 데이터)부터 역순으로 탐색
                for(int i = observations.size() - 1; i >= 0; i--) {
                    JsonNode node = observations.get(i);
                    String valueStr = node.get("value").asText();

                    // "." 은 데이터가 없음을 의미하므로 건너뜀
                    if (!".".equals(valueStr)) {
                        double value = Double.parseDouble(valueStr);
                        log.debug("Fetched {} : {}", indicator.name(), value);
                        return value;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error fetching/parsing seriesId: {}", indicator.getSeriesId(), e);
        }
        return null;
    }

    // API 응답(observations)을 순회하며 Map에 값을 채우는 헬퍼 메서드
    private void collectHistory(Map<LocalDate, Map<MacroIndicator, Double>> historyMap, MacroIndicator indicator) {
        try {
            JsonNode rootNode = dataFetchService.getMacroIndicator(indicator.getSeriesId());
            JsonNode observations = rootNode.get("observations");

            if (observations != null && observations.isArray()) {
                for (JsonNode node : observations) {
                    String valueStr = node.get("value").asText();
                    if (".".equals(valueStr)) continue; // "."은 데이터 없음

                    LocalDate date = LocalDate.parse(node.get("date").asText());
                    Double value = Double.parseDouble(valueStr);

                    // 해당 날짜의 맵을 가져오거나 생성
                    historyMap.putIfAbsent(date, new EnumMap<>(MacroIndicator.class));
                    historyMap.get(date).put(indicator, value);
                }
            }
        } catch (Exception e) {
            log.error("과거 기록을 처리하던 중 오류 발생 {}: {}", indicator.getSeriesId(), e.getMessage());
        }
    }
}

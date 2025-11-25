package com.companyvalue.companyvalue.service;

import com.companyvalue.companyvalue.domain.MacroEconomicData;
import com.companyvalue.companyvalue.domain.repository.MacroRepository;
import com.companyvalue.companyvalue.dto.MainResponseDto;
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

        // 날짜별 지표 값을 담을 임시 저장소 (Key: 날짜, Value: 지표별 값 Map)
        Map<LocalDate, Map<String, Double>> historyMap = new HashMap<>();

        // 1. 각 지표별 전체 데이터 파싱하여 Map에 병합
        collectHistory(historyMap, "DGS10", "us10y");
        collectHistory(historyMap, "DGS2", "us2y");
        collectHistory(historyMap, "DFF", "fedFunds");
        collectHistory(historyMap, "CPIAUCSL", "cpi");
        collectHistory(historyMap, "UNRATE", "unemployment");

        // 2. Map을 Entity로 변환하여 저장
        List<MacroEconomicData> dataList = new ArrayList<>();

        for (LocalDate date : historyMap.keySet()) {
            Map<String, Double> values = historyMap.get(date);

            // 이미 해당 날짜 데이터가 DB에 있는지 확인 (중복 방지)
            MacroEconomicData macroData = macroRepository.findByRecordedDate(date)
                    .orElseGet(() -> MacroEconomicData.builder()
                            .recordedDate(date)
                            .build());

            // 데이터 업데이트 (값이 없으면 기존 값 유지하거나 null)
            // getOrDefault를 사용하여 null 처리를 안전하게 합니다.
            macroData.updateData(
                    values.getOrDefault("fedFunds", macroData.getFedFundsRate()),
                    values.getOrDefault("us10y", macroData.getUs10yTreasuryYield()),
                    values.getOrDefault("us2y", macroData.getUs2yTreasuryYield()),
                    values.getOrDefault("cpi", macroData.getInflationRate()),
                    values.getOrDefault("unemployment", macroData.getUnemploymentRate())
            );

            dataList.add(macroData);
        }

        // 3. 일괄 저장
        macroRepository.saveAll(dataList);
        log.info("거시 경제 과거 데이터 초기화 완료: 총 {}건", dataList.size());
    }


    @Transactional
    public void updateMacroEconomicData() {
        log.info("거시 경제 정보 업데이트 시작...");
        //  각 지표별 최신 값 가져오기 (FRED API 호출)
        // DGS10: 10년물 국채,
        Double us10y = fetchLatestValue("DGS10");
//        DGS2: 2년물 국채,
        Double us2y = fetchLatestValue("DGS2");
//        DFF: 기준금리,
        Double fedFunds = fetchLatestValue("DFF");
//        CPIAUCSL: 소비자물가지수,
        Double cpi = fetchLatestValue("CPIAUCSL");
//        UNRATE: 실업률
        Double unemployment = fetchLatestValue("UNRATE");

        LocalDate today = LocalDate.now();

        // DB에 저장하기 (오늘 날짜 데이터가 이미 있으면 업데이트, 없으면 생성)
        MacroEconomicData macroData = macroRepository.findByRecordedDate(today)
                .orElseGet(() -> MacroEconomicData.builder()
                        .recordedDate(today)
                        .build());

        // 데이터 업데이트 및 저장
        macroData.updateData(fedFunds, us10y, us2y, cpi, unemployment);
        macroRepository.save(macroData);

        log.info("거시 경제 정보 최신화 완료: {}", today);
    }

    // 1. 최신 지표 조회 (DTO만 캐싱)
    @Cacheable(value = "macro_latest", key = "'latest'", unless = "#result == null")
    public MainResponseDto.MacroDataResponse getLatestData() {
        return macroRepository.findTopByOrderByRecordedDateDesc()
                .map(MainResponseDto.MacroDataResponse::from)
                .orElse(null);
    }

    // 2. 과거 데이터 조회 (DTO 리스트만 캐싱)
    @Cacheable(value = "macro_history", key = "'history'", unless = "#result == null")
    public List<MainResponseDto.MacroDataResponse> getHistoryData() {
        return macroRepository.findTop3650ByOrderByRecordedDateDesc()
                .stream()
                .map(MainResponseDto.MacroDataResponse::from)
                .sorted(Comparator.comparing(MainResponseDto.MacroDataResponse::date))
                .toList();
    }

    /**
     * API 응답 JSON에서 가장 최신의 유효한 값을 추출하는 헬퍼 메서드
     */
    private Double fetchLatestValue(String seriesId) {
        try {
            JsonNode rootNode = dataFetchService.getMacroIndicator(seriesId);
            JsonNode observations = rootNode.get("observations");

            if (observations != null && observations.isArray()) {
                // 배열의 끝(최신 데이터)부터 역순으로 탐색
                for (int i = observations.size() - 1; i >= 0; i--) {
                    JsonNode node = observations.get(i);
                    String valueStr = node.get("value").asText();

                    // "." 은 데이터가 없음을 의미하므로 건너뜀
                    if (!".".equals(valueStr)) {
                        double value = Double.parseDouble(valueStr);
                        log.debug("Fetched {} : {}", seriesId, value);
                        return value;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error fetching/parsing seriesId: {}", seriesId, e);
        }
        // 에러가 나거나 값이 없으면 null 반환 (나중에 null 체크 필요할 수 있음)
        return null;
    }

    // API 응답(observations)을 순회하며 Map에 값을 채우는 헬퍼 메서드
    private void collectHistory(Map<LocalDate, Map<String, Double>> historyMap, String seriesId, String keyName) {
        try {
            JsonNode rootNode = dataFetchService.getMacroIndicator(seriesId);
            JsonNode observations = rootNode.get("observations");

            if (observations != null && observations.isArray()) {
                for (JsonNode node : observations) {
                    String valueStr = node.get("value").asText();
                    if (".".equals(valueStr)) continue; // "."은 데이터 없음

                    LocalDate date = LocalDate.parse(node.get("date").asText());
                    Double value = Double.parseDouble(valueStr);

                    // 해당 날짜의 맵을 가져오거나 생성
                    historyMap.putIfAbsent(date, new HashMap<>());
                    historyMap.get(date).put(keyName, value);
                }
            }
        } catch (Exception e) {
            log.error("과거 기록을 처리하던 중 오류 발생 {}: {}", seriesId, e.getMessage());
        }
    }
}

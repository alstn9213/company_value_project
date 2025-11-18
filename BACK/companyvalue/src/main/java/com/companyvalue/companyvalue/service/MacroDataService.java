package com.companyvalue.companyvalue.service;

import com.companyvalue.companyvalue.domain.MacroEconomicData;
import com.companyvalue.companyvalue.repository.MacroRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class MacroDataService {

    private final MacroRepository macroRepository;
    private final DataFetchService dataFetchService;


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
}

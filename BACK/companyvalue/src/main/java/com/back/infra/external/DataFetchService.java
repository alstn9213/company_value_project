package com.back.infra.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataFetchService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper; // JSON 파싱용

    @Value("${api.fred.base-url}")
    private String fredBaseUrl;

    @Value("${api.fred.key}")
    private String fredKey;

    // --- api 호출 메서드 ---

    // --- FRED API 호출 (거시 경제 정보) ---
    public JsonNode getMacroIndicator(String seriesId) {
        String response = webClient.get()
                .uri(fredBaseUrl, uriBuilder -> uriBuilder
                        .queryParam("series_id", seriesId)
                        .queryParam("api_key", fredKey)
                        .queryParam("file_type", "json") // JSON 포맷 요청
                        .queryParam("observation_start", "2000-01-01")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return parseJson(response);
    }

    // --- 헬퍼 메서드 ---

    // JSON String -> JsonNode 변환 헬퍼 메서드
    private JsonNode parseJson(String jsonResponse) {
        try {
            return objectMapper.readTree(jsonResponse);
        } catch (Exception e) {
            log.error("JSON 파싱 에러: {}", e.getMessage());
            throw new RuntimeException("데이터 파싱 중 오류 발생");
        }
    }
}
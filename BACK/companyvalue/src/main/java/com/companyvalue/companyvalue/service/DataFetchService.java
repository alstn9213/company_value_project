package com.companyvalue.companyvalue.service;

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

    @Value("${api.alpha-vantage.base-url}")
    private String alphaBaseUrl;

    @Value("${api.alpha-vantage.key}")
    private String alphaKey;

    @Value("${api.fred.base-url}")
    private String fredBaseUrl;

    @Value("${api.fred.key}")
    private String fredKey;

    // ==========================================
    // 1. Alpha Vantage 호출 (기업 정보)
    // ==========================================
    public JsonNode getCompanyFinancials(String function, String symbol) {
        // 요청 URL 만들기: base-url + ?function=...&symbol=...&apikey=...
        String response = webClient.get()
                .uri(alphaBaseUrl, uriBuilder -> uriBuilder
                        .queryParam("function", function)
                        .queryParam("symbol", symbol)
                        .queryParam("apikey", alphaKey)
                        .build())
                .retrieve()
                .bodyToMono(String.class) // 결과(JSON)를 String으로 받음
                .block(); // (중요) 결과를 받을 때까지 기다림 (동기 처리)

        return parseJson(response);
    }

    // ==========================================
    // 2. FRED 호출 (거시 경제 정보)
    // ==========================================
    public JsonNode getMacroIndicator(String seriesId) {
        // 요청 URL 만들기: base-url + ?series_id=...&api_key=...&file_type=json
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

    // JSON String -> JsonNode 변환 메서드
    private JsonNode parseJson(String jsonResponse) {
        try {
            return objectMapper.readTree(jsonResponse);
        } catch (Exception e) {
            log.error("JSON 파싱 에러: {}", e.getMessage());
            throw new RuntimeException("데이터 파싱 중 오류 발생");
        }
    }
}
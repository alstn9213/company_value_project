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
    // 1. Alpha Vantage API 호출 (기업 정보)
    // ==========================================
    /**
     * 재무제표 데이터 가져오기 (INCOME_STATEMENT, BALANCE_SHEET, CASH_FLOW)
     */
    public JsonNode getCompanyFinancials(String function, String symbol) {
        return callAlphaVantage(function, symbol);
    }

    /**
     * 기업 개요 및 투자 지표 가져오기 (PER, PBR, 배당수익률 등)
     * Function: OVERVIEW
     */
    public JsonNode getCompanyOverview(String symbol) {
        return callAlphaVantage("OVERVIEW", symbol);
    }

    /**
     * 실시간 주가 정보 가져오기
     * Function: GLOBAL_QUOTE
     */
    public JsonNode getStockPrice(String symbol) {
        return callAlphaVantage("GLOBAL_QUOTE", symbol);
    }

    // 공통 호출 메서드 추출
    private JsonNode callAlphaVantage(String function, String symbol) {
        String response = webClient.get()
                .uri(alphaBaseUrl, uriBuilder -> uriBuilder
                        .queryParam("function", function)
                        .queryParam("symbol", symbol)
                        .queryParam("apikey", alphaKey)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return parseJson(response);
    }

    // ==========================================
    // 2. FRED API 호출 (거시 경제 정보)
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
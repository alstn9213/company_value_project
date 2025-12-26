package com.back.infra.external;

import com.back.infra.external.dto.ExternalFinancialDataResponse;
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

    // api 호출 제한 설정
    // 기본값은 false (제한 없음)
    @Value("${api.alpha-vantage.dev-mode-limit:false}")
    private boolean isDevModeLimit;


    // --- api 호출 메서드 ---
    // 1. Alpha Vantage API 호출 (기업 정보)

    //  3가지 재무제표를 가져오는 메서드
    public ExternalFinancialDataResponse getCombinedFinancialData(String ticker) {
        log.info("Alpha Vantage API 통합 호출 시작: {}", ticker);

        JsonNode income = getCompanyFinancials("INCOME_STATEMENT", ticker);
        JsonNode balance = getCompanyFinancials("BALANCE_SHEET", ticker);
        JsonNode cash = getCompanyFinancials("CASH_FLOW", ticker);

        return new ExternalFinancialDataResponse(income, balance, cash);
    }


    // 기업 개요 및 투자 지표 가져오는 메서드 (PER, PBR, 배당수익률 등)
    public JsonNode getCompanyOverview(String symbol) {
        return callAlphaVantage("OVERVIEW", symbol);
    }

    // 일별 주가 데이터 가져오는 메서드 (차트용)
    public JsonNode getDailyStockHistory(String symbol) {
        return callAlphaVantage("TIME_SERIES_DAILY", symbol);
    }


    // 2. FRED API 호출 (거시 경제 정보)
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
    // 기업 정보 호출 헬퍼 메서드
    private JsonNode callAlphaVantage(String function, String symbol) {
        if(isDevModeLimit && !"AAPL".equals(symbol)) {
            log.info("[개발 모드] API 호출 횟수 절약을 위해 '{}' 요청을 건너뜁니다. (설정: api.alpha-vantage.dev-mode-limit=true)", symbol);
            return objectMapper.createObjectNode(); // 빈 객체생성
        }

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

    // 재무제표 가져오는 헬퍼 메서드
    private JsonNode getCompanyFinancials(String function, String symbol) {
        return callAlphaVantage(function, symbol);
    }

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
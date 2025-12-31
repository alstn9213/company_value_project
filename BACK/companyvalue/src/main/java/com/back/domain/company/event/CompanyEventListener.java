package com.back.domain.company.event;

import com.back.domain.company.entity.Company;
import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.entity.StockPriceHistory;
import com.back.domain.company.repository.CompanyRepository;
import com.back.domain.company.repository.FinancialStatementRepository;
import com.back.domain.company.repository.StockPriceHistoryRepository;
import com.back.domain.company.service.analysis.ScoringService;
import com.back.global.error.ErrorCode;
import com.back.global.error.exception.BusinessException;
import com.back.infra.external.DataFetchService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Component
@RequiredArgsConstructor
public class CompanyEventListener {

    private final ScoringService scoringService;
    private final FinancialStatementRepository financialStatementRepository;
    private final CompanyRepository companyRepository;
    private final StockPriceHistoryRepository stockPriceHistoryRepository;
    private final DataFetchService dataFetchService;
    private final ObjectMapper objectMapper;

    private static final long ASSUMED_TOTAL_SHARES = 100_000_000L;
    private static final BigDecimal DEFAULT_PRICE = BigDecimal.valueOf(10.0);

    // 기업 재무 데이터 업데이트 이벤트 발생 시 실행되는 점수 계산 메서드
    // Overview 데이터를 가져오고 점수 계산
    @EventListener
    public void handleFinancialsUpdated(CompanyFinancialsUpdatedEvent event) {
        String ticker = event.ticker();
        log.info(">>> [Event] 점수 계산 트리거 감지: {}", ticker);
        try {
            processScoring(ticker);
        } catch (Exception e) {
            log.error(">>> [Event] 점수 계산 중 예외 발생 - Ticker: {}", ticker, e);
        }
    }

    // 점수 계산 및 저장 헬퍼 메서드
    private void processScoring(String ticker) {
        Company company = companyRepository.findByTicker(ticker)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMPANY_NOT_FOUND));

        FinancialStatement fs = financialStatementRepository.findTopByCompanyOrderByYearDescQuarterDesc(company)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_FINANCIAL_DATA));

        JsonNode overview = fetchOverviewSafely(ticker, fs);
        scoringService.calculateAndSaveScore(fs, overview);
        log.info(">>> [Event] 점수 계산 및 저장 완료: {}", ticker);
    }

    // 실제 Overview 정보를 가져오거나 더미 데이터를 생성하는 헬퍼 메서드
    private JsonNode fetchOverviewSafely(String ticker, FinancialStatement fs) {
        try {
            return dataFetchService.getCompanyOverview(ticker);
        } catch (Exception e) {
            log.warn("Overview API 조회 실패 또는 가짜 기업 ({}). 더미 Overview를 생성합니다.", ticker);
            return createCalculatedDummyOverview(ticker, fs);
        }
    }

    // DB에 저장된 더미 데이터에 기반해서 더미 overview를 생성하는 헬퍼 메서드
    private JsonNode createCalculatedDummyOverview(String ticker, FinancialStatement fs) {
        ObjectNode node = objectMapper.createObjectNode();
        StockPriceHistory latestStock = stockPriceHistoryRepository.findTopByCompanyOrderByRecordedDateDesc(fs.getCompany());

        BigDecimal price;
        if(latestStock == null) {
            log.warn(">>> [Data Warning] {}의 주가 데이터가 아직 생성되지 않았거나 없습니다. 기본값으로 처리합니다.", ticker);
            price = DEFAULT_PRICE;
        } else {
            price = latestStock.getClosePrice();
        }

        BigDecimal shares = BigDecimal.valueOf(ASSUMED_TOTAL_SHARES);

        // --- 주요 지표 계산 ---
        // 시가총액 = 주가 * 발행주식수
        BigDecimal marketCap = price.multiply(shares);

        // EPS (주당 순이익) = (분기 순이익 * 4) / 주식수  (연간 환산 가정)
        BigDecimal annualNetIncome = fs.getNetIncome().multiply(BigDecimal.valueOf(4));
        BigDecimal eps = annualNetIncome.divide(shares, 2, RoundingMode.HALF_UP);

        // PER (주가 수익 비율) = 주가 / EPS
        BigDecimal peRatio = (eps.compareTo(BigDecimal.ZERO) > 0)
                ? price.divide(eps, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // PBR (주가 순자산 비율) = 시가총액 / 자본총계
        BigDecimal bookValue = fs.getTotalEquity();
        BigDecimal pbRatio = (bookValue.compareTo(BigDecimal.ZERO) > 0)
                ? marketCap.divide(bookValue, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // 배당 수익률
        String dividendYield = "0.015";

        // JSON 생성
        node.put("Symbol", ticker);
        node.put("PERatio", peRatio.toString());
        node.put("PriceToBookRatio", pbRatio.toString());
        node.put("MarketCapitalization", marketCap.toString());
        node.put("DividendYield", dividendYield);
        node.put("EPS", eps.toString());

        log.debug(">>> [Dummy] 생성된 Overview ({}): PER={}, PBR={}", ticker, peRatio, pbRatio);

        return node;
    }
}
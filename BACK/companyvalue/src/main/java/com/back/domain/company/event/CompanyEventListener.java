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
    private final ObjectMapper objectMapper;
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

        JsonNode overview = calculateOverview(ticker, fs);
        scoringService.calculateAndSaveScore(fs, overview);
        log.info(">>> [Event] 점수 계산 및 저장 완료: {}", ticker);
    }

    // DB 데이터를 사용하여 지표(PER, PBR, MarketCap) 계산
    private JsonNode calculateOverview(String ticker, FinancialStatement fs) {
        ObjectNode node = objectMapper.createObjectNode();

        // --- 주식 수 가져오기 (없으면 기본값 1억 주 방어 로직) ---
        Long totalShares = fs.getCompany().getTotalShares();
        if(totalShares == null || totalShares == 0) totalShares = 100_000_000L;
        BigDecimal shares = BigDecimal.valueOf(totalShares);

        // 최신 주가 조회
        StockPriceHistory latestStock = stockPriceHistoryRepository.findTopByCompanyOrderByRecordedDateDesc(fs.getCompany());

        BigDecimal price;
        if(latestStock == null) {
            log.warn("[Calculation] {} 주가 데이터 없음. 기본값({}) 사용", ticker, DEFAULT_PRICE);
            price = DEFAULT_PRICE;
        } else {
            price = latestStock.getClosePrice();
        }

        // --- 계산 로직 ---
        // 시가총액
        BigDecimal marketCap = price.multiply(shares);

        // EPS (연간 환산)
        BigDecimal annualNetIncome = fs.getNetIncome().multiply(BigDecimal.valueOf(4));
        BigDecimal eps = annualNetIncome.divide(shares, 2, RoundingMode.HALF_UP);

        // PER
        BigDecimal peRatio = (eps.compareTo(BigDecimal.ZERO) > 0)
                ? price.divide(eps, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // PBR
        BigDecimal bookValue = fs.getTotalEquity();
        BigDecimal pbRatio = (bookValue.compareTo(BigDecimal.ZERO) > 0)
                ? marketCap.divide(bookValue, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // 배당 수익률 (임시 고정값)
        String dividendYield = "0.015";

        // JSON 생성
        node.put("Symbol", ticker);
        node.put("PERatio", peRatio.toString());
        node.put("PriceToBookRatio", pbRatio.toString());
        node.put("MarketCapitalization", marketCap.toString());
        node.put("DividendYield", dividendYield);
        node.put("EPS", eps.toString());

        return node;
    }

}
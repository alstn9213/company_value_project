package com.back.domain.company.event;

import com.back.domain.company.entity.Company;
import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.repository.CompanyRepository;
import com.back.domain.company.repository.FinancialStatementRepository;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class CompanyEventListener {

    private final ScoringService scoringService;
    private final FinancialStatementRepository financialStatementRepository;
    private final CompanyRepository companyRepository;
    private final DataFetchService dataFetchService;
    private final ObjectMapper objectMapper;

    // 기업 재무 데이터 업데이트 이벤트 발생 시 실행되는 점수 계산 메서드
    // Overview 데이터를 가져오고 점수 계산
    @EventListener
    public void handleFinancialsUpdated(CompanyFinancialsUpdatedEvent event) {
        String ticker = event.ticker();
        log.info(">>> [Event] 점수 계산 트리거 감지: {}", ticker);
        try {
            processScoring(ticker);
        } catch (Exception e) {
            log.error(">>> [Event] 점수 계산 실패 ({}): {}", ticker, e.getMessage());
        }
    }

    // -- 내부 메서드 --

    // 점수 계산 내부 메서드
    private void processScoring(String ticker) {
        Company company = companyRepository.findByTicker(ticker)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMPANY_NOT_FOUND));

        FinancialStatement fs = financialStatementRepository.findTopByCompanyOrderByYearDescQuarterDesc(company)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_FINANCIAL_DATA));

        JsonNode overview = fetchOverviewSafely(ticker);
        scoringService.calculateAndSaveScore(fs, overview);
        log.info(">>> [Event] 점수 계산 및 저장 완료: {}", ticker);
    }

    // 실제 Overview 정보를 가져오거나 더미 데이터를 생성하는 내부 메서드
    private JsonNode fetchOverviewSafely(String ticker) {
        try {
            return dataFetchService.getCompanyOverview(ticker);
        } catch (Exception e) {
            log.warn("Overview API 조회 실패 (가짜 기업 또는 API 오류): {}, 더미 데이터를 사용합니다.", ticker);
            return createDummyOverview(ticker);
        }
    }

    // 가짜 기업용 더미 Overview 생성하는 내부 메서드
    private JsonNode createDummyOverview(String ticker) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("Symbol", ticker);
        node.put("PERatio", "15.0");
        node.put("PriceToBookRatio", "2.0");
        node.put("MarketCapitalization", "1000000000");
        node.put("DividendYield", "0.02");
        node.put("EPS", "5.0");
        return node;
    }
}
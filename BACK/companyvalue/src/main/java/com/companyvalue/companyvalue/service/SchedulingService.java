package com.companyvalue.companyvalue.service;

import com.companyvalue.companyvalue.domain.Company;
import com.companyvalue.companyvalue.domain.FinancialStatement;
import com.companyvalue.companyvalue.domain.repository.CompanyRepository;
import com.companyvalue.companyvalue.domain.repository.CompanyScoreRepository;
import com.companyvalue.companyvalue.domain.repository.FinancialStatementRepository;
import com.companyvalue.companyvalue.dto.FinancialDataDto;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulingService {

    private final CompanyRepository companyRepository;
    private final FinancialDataService financialDataService;
    private final ScoringService scoringService;
    private final MacroDataService macroDataService;
    private final FinancialStatementRepository financialStatementRepository;
    private final DataFetchService dataFetchService;
    private final CompanyScoreRepository companyScoreRepository;

    // ==========================================
    // 1. 거시 경제 지표 자동 업데이트 (매일 아침 8시)
    // ==========================================
    @Scheduled(cron = "0 0 8 * * *")
    @CacheEvict(value = {"macro_latest", "macro_history"}, allEntries = true) // 이 메서드가 실행될 때 캐시 전체 삭제
    public void updateMacroData() {
        log.info(">>> [Scheduler] 거시 경제 데이터 업데이트 시작 (캐시 초기화 포함)");
        try {
            macroDataService.updateMacroEconomicData();
            log.info(">>> [Scheduler] 거시 경제 데이터 업데이트 완료");
        } catch (Exception e) {
            log.error(">>> [Scheduler] 거시 경제 업데이트 실패", e);
        }
    }

    // ==========================================
    // 2. 기업 재무 정보 및 점수 자동 업데이트 (매주 일요일 새벽 2시)
    // ==========================================
    @Scheduled(cron = "0 0 2 * * SUN")
    @CacheEvict(value = "company_score", allEntries = true) // 모든 기업 점수 캐시 삭제
    public void updateFinancialsAndScores() {
        log.info(">>> [Scheduler] 기업 재무/점수 일괄 업데이트 시작 (캐시 초기화 포함)");
        executeAllCompaniesUpdate();
        log.info(">>> [Scheduler] 기업 재무/점수 일괄 업데이트 종료");
    }

    // 실제 로직을 수행하는 메서드 (테스트 컨트롤러에서도 호출 가능하도록 분리)
    public void executeAllCompaniesUpdate() {
        List<Company> companies = companyRepository.findAll();
        for(Company company : companies) {
            String ticker = company.getTicker();
            boolean hasFinancials = financialStatementRepository
                    .findTopByCompanyOrderByYearDescQuarterDesc(company)
                    .isPresent();
            boolean hasScore = companyScoreRepository.findByCompany(company).isPresent();

            if(hasFinancials && hasScore) continue;

            try {
                if (!hasFinancials) {
                    FinancialDataDto rawData = financialDataService.fetchRawFinancialData(ticker);
                    financialDataService.saveFinancialData(ticker, rawData);
                    Thread.sleep(12000); // API 무료 키 제한 고려
                }
                // 점수 계산 (재무제표가 방금 저장되었거나, 이미 있었는데 점수만 없는 경우 모두 수행)
                FinancialStatement fs = financialStatementRepository.findTopByCompanyOrderByYearDescQuarterDesc(company)
                        .orElseThrow(() -> new RuntimeException("재무제표 없음: " + ticker));
                JsonNode overview = dataFetchService.getCompanyOverview(ticker);
                scoringService.calculateAndSaveScore(fs, overview);

                if(hasFinancials) Thread.sleep(12000); // 점수만 계산하는 경우에도 API 호출했으므로 대기

            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                log.error("스레드 대기 중 인터럽트 발생", ie);
            } catch (Exception e) {
                log.error("처리 중 오류 발생 ({}): {}", ticker, e.getMessage());
                // 한 기업이 실패해도 다음 기업으로 계속 진행 (continue)
            }
        }
    }
}
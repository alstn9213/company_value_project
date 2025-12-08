package com.back.infra.scheduler;

import com.back.domain.company.entity.Company;
import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.repository.CompanyRepository;
import com.back.domain.company.repository.CompanyScoreRepository;
import com.back.domain.company.repository.FinancialStatementRepository;
import com.back.domain.company.service.finance.FinancialDataSyncService;
import com.back.domain.company.service.finance.FinancialStatementService;
import com.back.infra.external.dto.ExternalFinancialDataResponse;
import com.back.domain.macro.service.MacroDataService;
import com.back.infra.external.DataFetchService;
import com.back.domain.company.service.analysis.ScoringService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulingService {

    private final CompanyRepository companyRepository;
    private final FinancialDataSyncService financialDataSyncService;
    private final MacroDataService macroDataService;

    // 점수 여부 확인용 (최적화를 위해)
    private final FinancialStatementRepository financialStatementRepository;
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

    public void executeAllCompaniesUpdate() {
        List<Company> companies = companyRepository.findAll();
        log.info(">>> [Scheduler] 대상 기업 수: {}", companies.size());

        for(Company company : companies) {
            String ticker = company.getTicker();
            boolean hasFinancials = financialStatementRepository
                    .findTopByCompanyOrderByYearDescQuarterDesc(company)
                    .isPresent();
            boolean hasScore = companyScoreRepository.findByCompany(company).isPresent();

            if(hasFinancials && hasScore) continue;

            try {
                // 데이터 동기화 요청
                // 내부에서 데이터 저장이 완료되면 Event가 발행되어, Listener가 점수를 계산함
                financialDataSyncService.synchronizeCompany(ticker);

                // API Rate Limit 조절 (API 호출이 일어났을 경우를 대비)
                // 실제 호출 여부와 관계없이 안전하게 대기 (또는 SyncService 반환값으로 제어 가능)
                if(!hasFinancials) {
                    Thread.sleep(12000);
                }

            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                log.error("스레드 인터럽트", ie);
            } catch (Exception e) {
                log.error("스케줄러 처리 실패 ({}): {}", ticker, e.getMessage());
            }
        }
    }
}
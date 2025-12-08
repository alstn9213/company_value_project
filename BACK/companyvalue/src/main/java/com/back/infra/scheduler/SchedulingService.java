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
    private final FinancialStatementService financialStatementService;
    private final ScoringService scoringService;
    private final MacroDataService macroDataService;
    private final FinancialStatementRepository financialStatementRepository;
    private final DataFetchService dataFetchService;
    private final CompanyScoreRepository companyScoreRepository;
    private final ObjectMapper objectMapper;

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
        log.info(">>> [Scheduler] 대상 기업 수: {}", companies.size());

        for(Company company : companies) {
            String ticker = company.getTicker();
            boolean hasFinancials = financialStatementRepository
                    .findTopByCompanyOrderByYearDescQuarterDesc(company)
                    .isPresent();
            boolean hasScore = companyScoreRepository.findByCompany(company).isPresent();

            if(hasFinancials && hasScore) continue;

            try {
                if(!hasFinancials) {
                    try {
                        ExternalFinancialDataResponse rawData = financialDataSyncService.fetchRawFinancialData(ticker);
                        financialStatementService.saveFinancialStatements(company, rawData);
                        Thread.sleep(12000); // API 무료 키 제한 고려
                    } catch (Exception e) {
                        // 가짜 기업은 API 호출 실패함. 실패해도 로그만 남기고 다음 단계(점수 계산)로 진행 시도
                        // InitDataConfig에서 더미 데이터를 넣었으므로 실제로는 hasFinancials가 true일 가능성이 높음
                        // 만약 진짜 API 실패이고 데이터도 없다면 아래 점수 계산에서 터지므로 catch
                        log.warn("재무 데이터 동기화 실패 (가짜 기업이거나 API 오류): {}", ticker);
                    }
                }
                // 점수 계산 로직
                // DB에서 가장 최근 재무제표 조회
                FinancialStatement fs = financialStatementRepository.findTopByCompanyOrderByYearDescQuarterDesc(company)
                        .orElse(null);

                if(fs == null) {
                    log.error("점수 계산 불가 (재무제표 없음): {}", ticker);
                    continue;
                }

                // Overview 데이터(PER, 시총 등) 조회
                JsonNode overview;
                try {
                    overview = dataFetchService.getCompanyOverview(ticker);
                } catch (Exception e) {
                    log.warn("Overview API 조회 실패 (가짜 기업): {}, 더미 Overview를 사용합니다.", ticker);
                    // 가짜 기업을 위해 가짜 Overview 생성
                    overview = createDummyOverview(ticker);
                }

                scoringService.calculateAndSaveScore(fs, overview);
                log.info("점수 계산 완료: {}", ticker);

                // API 호출을 성공적으로 수행했다면 대기 (가짜 기업은 API 호출 안 했으므로 대기 불필요하지만 안전하게 적용)
                if(!hasFinancials) Thread.sleep(2000);

            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                log.error("스레드 대기 중 인터럽트 발생", ie);
            } catch (Exception e) {
                log.error("처리 중 오류 발생 ({}): {}", ticker, e.getMessage());
            }
        }
    }
        // 가짜 기업용 더미 Overview 생성 (점수 계산 시 NPE 방지)
        private JsonNode createDummyOverview(String ticker) {
            ObjectNode node = objectMapper.createObjectNode();
            node.put("Symbol", ticker);
            node.put("PERatio", "15.0");       // 평균적인 PER
            node.put("PriceToBookRatio", "2.0"); // 평균적인 PBR
            node.put("MarketCapitalization", "1000000000"); // 10억 달러
            node.put("DividendYield", "0.02"); // 2% 배당
            node.put("EPS", "5.0");
            return node;
        }
}
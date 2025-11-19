package com.companyvalue.companyvalue.service;

import com.companyvalue.companyvalue.domain.Company;
import com.companyvalue.companyvalue.domain.FinancialStatement;
import com.companyvalue.companyvalue.domain.repository.CompanyRepository;
import com.companyvalue.companyvalue.domain.repository.FinancialStatementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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

    // ==========================================
    // 1. 거시 경제 지표 자동 업데이트 (매일 아침 8시)
    // ==========================================
    @Scheduled(cron = "0 0 8 * * *")
    public void updateMacroData() {
        log.info(">>> [Scheduler] 거시 경제 데이터 업데이트 시작");
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
    public void updateFinancialsAndScores() {
        log.info(">>> [Scheduler] 기업 재무/점수 일괄 업데이트 시작");
        executeAllCompaniesUpdate();
        log.info(">>> [Scheduler] 기업 재무/점수 일괄 업데이트 종료");
    }

    // 실제 로직을 수행하는 메서드 (테스트 컨트롤러에서도 호출 가능하도록 분리)
    public void executeAllCompaniesUpdate() {
        // 1. DB에 있는 모든 기업 가져오기
        List<Company> companies = companyRepository.findAll();

        for (Company company : companies) {
            String ticker = company.getTicker();
            log.info("--- Processing: {} ---", ticker);

            try {
                // 2. 재무제표 API 호출 및 업데이트
                financialDataService.updateCompanyFinancials(ticker);

                // 3. API Rate Limit 방지 (무료 키: 분당 5회 제한 -> 15초 대기)
                Thread.sleep(15000);

                // 4. 업데이트된 최신 재무제표 조회
                FinancialStatement fs = financialStatementRepository.findTopByCompanyOrderByYearDescQuarterDesc(company)
                        .orElseThrow(() -> new RuntimeException("재무제표 없음: " + ticker));

                // 5. 점수 계산 및 저장
                scoringService.calculateAndSaveScore(fs);

                log.info("--- Completed: {} ---", ticker);

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
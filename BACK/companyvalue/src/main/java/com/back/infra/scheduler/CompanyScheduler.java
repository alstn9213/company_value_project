package com.back.infra.scheduler;

import com.back.infra.scheduler.service.CompanyBatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CompanyScheduler {

    private final CompanyBatchService companyBatchService;

    // ==========================================
    // 기업 재무 정보 및 점수 자동 업데이트 (매주 일요일 새벽 2시)
    // ==========================================
    @Scheduled(cron = "0 0 2 * * SUN")
    @CacheEvict(value = "company_score", allEntries = true) // 모든 기업 점수 캐시 삭제
    public void updateFinancialsAndScores() {
        log.info(">>> [Scheduler] 기업 재무 정보 및 점수 일괄 업데이트 시작 (캐시 초기화 포함)");
        companyBatchService.executeAllCompaniesUpdate();
        log.info(">>> [Scheduler] 기업 재무 정보 및 점수 일괄 업데이트 종료");
    }
}
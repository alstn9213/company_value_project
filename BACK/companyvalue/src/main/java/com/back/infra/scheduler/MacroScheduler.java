package com.back.infra.scheduler;

import com.back.domain.macro.service.MacroDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MacroScheduler {

    private final MacroDataService macroDataService;

    // ==========================================
    // 거시 경제 지표 자동 업데이트 (매일 아침 8시)
    // ==========================================
    @Scheduled(cron = "0 0 8 * * *")
    @CacheEvict(value = {"macro_latest", "macro_history"}, allEntries = true)
    public void updateMacroData() {
        log.info(">>> [Scheduler] 거시 경제 데이터 업데이트 시작 (캐시 초기화 포함)");
        try {
            macroDataService.updateMacroEconomicData();
            log.info(">>> [Scheduler] 거시 경제 데이터 업데이트 완료");
        } catch (Exception e) {
            log.error(">>> [Scheduler] 거시 경제 업데이트 실패", e);
        }
    }
}
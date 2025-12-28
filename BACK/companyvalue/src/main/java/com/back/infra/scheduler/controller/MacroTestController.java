package com.back.infra.scheduler.controller;

import com.back.domain.macro.service.MacroDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/test/macro")
@RequiredArgsConstructor
public class MacroTestController {

    private final MacroDataService macroDataService;

    /**
     * DB를 구성할 때 딱 한번만 사용하는 거시 경제 최신 데이터 일괄 초기화 메서드
     * URL: http://localhost:8080/test/macro/init
     */
    @GetMapping("/init")
    public String initMacroHistory() {
        macroDataService.initHistoricalMacroData();
        return "거시 경제 과거 데이터 초기화 완료.";
    }

    /**
     * 거시 경제 최신 데이터 수동 업데이트 메서드
     * URL: http://localhost:8080/test/macro/update-daily
     */
    @GetMapping("/update-daily")
    public String updateDailyMacro() {
        log.info(">>> [Test] 거시 경제 데이터 일일 업데이트 요청");
        try {
            macroDataService.updateMacroEconomicData();
            return "거시 경제 데이터(금리/물가/실업률) 업데이트 및 캐시 초기화 완료.";
        } catch (Exception e) {
            log.error("업데이트 실패", e);
            return "업데이트 실패: " + e.getMessage();
        }
    }
}


package com.back.infra.scheduler;

import com.back.domain.macro.service.MacroDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ScheduleTestController {

    private final SchedulingService schedulingService;
    private final MacroDataService macroDataService;

//    http://localhost:8080/test/macro/init
    @GetMapping("/test/macro/init")
    public String initMacroHistory() {
        macroDataService.initHistoricalMacroData();
        return "거시 경제 과거 데이터 초기화 완료.";
    }

    // http://localhost:8080/test/schedule/run
    @GetMapping("/test/schedule/run")
    public String runScheduleManually() {
        schedulingService.executeAllCompaniesUpdate();
        return "모든 기업 업데이트 완료.";
    }
}
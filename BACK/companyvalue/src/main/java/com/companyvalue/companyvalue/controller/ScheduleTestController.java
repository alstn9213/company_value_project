package com.companyvalue.companyvalue.controller;

import com.companyvalue.companyvalue.service.SchedulingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ScheduleTestController {

    private final SchedulingService schedulingService;

    // http://localhost:8080/test/schedule/run 으로 접속하면 즉시 실행
    @GetMapping("/test/schedule/run")
    public String runScheduleManually() {
        // 비동기 처리를 안 했으므로, 이 메서드가 끝날 때까지 브라우저가 로딩 상태일 것입니다.
        // (기업이 많으면 타임아웃 날 수 있음, 로그로 진행상황 확인 추천)
        schedulingService.executeAllCompaniesUpdate();
        return "모든 기업 업데이트 완료! 로그를 확인하세요.";
    }
}
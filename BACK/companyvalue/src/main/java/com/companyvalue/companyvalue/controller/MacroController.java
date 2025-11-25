package com.companyvalue.companyvalue.controller;

import com.companyvalue.companyvalue.dto.MainResponseDto;
import com.companyvalue.companyvalue.service.MacroDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/macro")
@RequiredArgsConstructor
public class MacroController {

    private final MacroDataService macroDataService;

    // 1. 대시보드용 최신 지표
    @GetMapping("/latest")
    public ResponseEntity<MainResponseDto.MacroDataResponse> getLatestMacroData() {
        // 서비스(캐시 적용됨) 호출
        MainResponseDto.MacroDataResponse data = macroDataService.getLatestData();

        if (data == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(data);
    }

    // 2. 차트용 10년치 과거 데이터
    @GetMapping("/history")
    public ResponseEntity<List<MainResponseDto.MacroDataResponse>> getMacroHistory() {
        // 서비스(캐시 적용됨) 호출
        List<MainResponseDto.MacroDataResponse> history = macroDataService.getHistoryData();
        return ResponseEntity.ok(history);
    }

}

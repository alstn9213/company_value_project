package com.back.api.macro;

import com.back.domain.macro.dto.MacroDataResponse;
import com.back.domain.macro.service.MacroReadService;
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

    private final MacroReadService macroReadService;

    // 대시보드용 최신 지표
    @GetMapping("/latest")
    public ResponseEntity<MacroDataResponse> getLatestMacroData() {
        return ResponseEntity.ok(macroReadService.getLatestData());
    }

    // 차트용 10년치 과거 데이터
    @GetMapping("/history")
    public ResponseEntity<List<MacroDataResponse>> getMacroHistory() {
        return ResponseEntity.ok(macroReadService.getHistoryData());
    }

}

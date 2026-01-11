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
        MacroDataResponse data = macroReadService.getLatestData();
        if(data == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(data);
    }

    // 차트용 10년치 과거 데이터
    @GetMapping("/history")
    public ResponseEntity<List<MacroDataResponse>> getMacroHistory() {
        List<MacroDataResponse> history = macroReadService.getHistoryData();
        return ResponseEntity.ok(history);
    }

}

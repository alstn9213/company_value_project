package com.companyvalue.companyvalue.controller;

import com.companyvalue.companyvalue.domain.repository.MacroRepository;
import com.companyvalue.companyvalue.dto.MainResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/macro")
@RequiredArgsConstructor
public class MacroController {

    private final MacroRepository macroRepository;

    // 1. 대시보드용 최신 지표 1건
    @GetMapping("/latest")
    public ResponseEntity<MainResponseDto.MacroDataResponse> getLatestMacroData() {
        return macroRepository.findTopByOrderByRecordedDateDesc()
                .map(MainResponseDto.MacroDataResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    // 2. 차트용 과거 데이터 (최근 10년)
    @GetMapping("/history")
    public ResponseEntity<List<MainResponseDto.MacroDataResponse>> getMacroHistory() {
        List<MainResponseDto.MacroDataResponse> history = macroRepository.findTop3650ByOrderByRecordedDateDesc()
                .stream()
                .map(MainResponseDto.MacroDataResponse::from)
                .sorted(Comparator.comparing(MainResponseDto.MacroDataResponse::date)) // 날짜 오름차순(과거 -> 현재)로 정렬하여 반환
                .toList();

        return ResponseEntity.ok(history);

    }

}

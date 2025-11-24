package com.companyvalue.companyvalue.controller;

import com.companyvalue.companyvalue.domain.repository.MacroRepository;
import com.companyvalue.companyvalue.dto.MainResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
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

    // 1. 대시보드용 최신 지표 1건 (캐시 이름: macro_latest, 키: 'latest')
    @GetMapping("/latest")
    @Cacheable(value = "macro_latest", key = "'latest'",unless = "#result == null")
    public ResponseEntity<MainResponseDto.MacroDataResponse> getLatestMacroData() {
        // 캐시에 데이터가 있으면 이 메서드 내부 코드는 실행되지 않고 캐시 값을 바로 반환한다.
        return macroRepository.findTopByOrderByRecordedDateDesc()
                .map(MainResponseDto.MacroDataResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    // 2. 차트용 10년치 과거 데이터 (캐시 이름: macro_history, 키: 'history')
    @GetMapping("/history")
    @Cacheable(value = "macro_history", key = "'history'", unless = "#result == null")
    public ResponseEntity<List<MainResponseDto.MacroDataResponse>> getMacroHistory() {
        List<MainResponseDto.MacroDataResponse> history = macroRepository.findTop3650ByOrderByRecordedDateDesc()
                .stream()
                .map(MainResponseDto.MacroDataResponse::from)
                .sorted(Comparator.comparing(MainResponseDto.MacroDataResponse::date)) // 날짜 오름차순(과거 -> 현재)로 정렬하여 반환
                .toList();

        return ResponseEntity.ok(history);

    }

}

package com.back.domain.macro.service;

import com.back.domain.macro.dto.MacroDataResponse;
import com.back.domain.macro.repository.MacroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MacroReadService {

    private final MacroRepository macroRepository;

    // 최신 지표 조회
    @Cacheable(value = "macro_latest", key = "'latest'", unless = "#result == null")
    public MacroDataResponse getLatestData() {
        return macroRepository.findTopByOrderByRecordedDateDesc()
                .map(MacroDataResponse::from)
                .orElse(null);
    }

    // 과거 데이터 조회
    @Cacheable(value = "macro_history", key = "'history'", unless = "#result == null")
    public List<MacroDataResponse> getHistoryData() {
        return macroRepository.findTop3650ByOrderByRecordedDateDesc()
                .stream()
                .map(MacroDataResponse::from)
                .sorted(Comparator.comparing(MacroDataResponse::date))
                .toList();
    }


}

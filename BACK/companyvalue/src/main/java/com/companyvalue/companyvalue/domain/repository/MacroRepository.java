package com.companyvalue.companyvalue.domain.repository;

import com.companyvalue.companyvalue.domain.MacroEconomicData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MacroRepository extends JpaRepository<MacroEconomicData, Long> {
    // 가장 최신 거시 경제 지표 1건 조회
    Optional<MacroEconomicData> findTopByOrderByRecordedDateDesc();
    Optional<MacroEconomicData> findByRecordedDate(LocalDate recordedDate);

    // 차트용: 최근 30일치 데이터 조회
    // 날짜 오름차순으로 정렬해서 주면 좋지만, DB 인덱스상 내림차순이 빠르므로 가져와서 뒤집음
    List<MacroEconomicData> findTop30ByOrderByRecordedDateDesc();
}

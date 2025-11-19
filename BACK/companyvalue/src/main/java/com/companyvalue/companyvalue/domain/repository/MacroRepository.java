package com.companyvalue.companyvalue.domain.repository;

import com.companyvalue.companyvalue.domain.MacroEconomicData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface MacroRepository extends JpaRepository<MacroEconomicData, Long> {
    // 가장 최신 거시 경제 지표 1건 조회
    Optional<MacroEconomicData> findTopByOrderByRecordedDateDesc();
    Optional<MacroEconomicData> findByRecordedDate(LocalDate recordedDate);
}

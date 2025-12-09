package com.back.domain.company.repository;

import com.back.domain.company.entity.Company;
import com.back.domain.company.entity.StockPriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface StockPriceHistoryRepository extends JpaRepository<StockPriceHistory, Long> {
    // 특정 기업의 주가 기록 조회 (날짜 오름차순 - 차트 그리기 용)
    List<StockPriceHistory> findByCompanyOrderByRecordedDateAsc(Company company);

    boolean existsByCompanyAndRecordedDate(Company company, LocalDate date);

    // 해당 기업의 가장 최근 주가 1개 조회 (점수 계산용)
    StockPriceHistory findTopByCompanyOrderByRecordedDateDesc(Company company);
}
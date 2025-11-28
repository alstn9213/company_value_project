package com.companyvalue.companyvalue.domain.repository;

import com.companyvalue.companyvalue.domain.Company;
import com.companyvalue.companyvalue.domain.StockPriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface StockPriceHistoryRepository extends JpaRepository<StockPriceHistory, Long> {
    // 특정 기업의 주가 기록 조회 (날짜 오름차순 - 차트 그리기 용)
    List<StockPriceHistory> findByCompanyOrderByRecordedDateAsc(Company company);

    boolean existsByCompanyAndRecordedDate(Company company, LocalDate date);
}
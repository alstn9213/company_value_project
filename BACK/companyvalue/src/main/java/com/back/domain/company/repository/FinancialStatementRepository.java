package com.back.domain.company.repository;

import com.back.domain.company.entity.Company;
import com.back.domain.company.entity.FinancialStatement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FinancialStatementRepository extends JpaRepository<FinancialStatement, Long> {

    // 특정 기업의 가장 최근 재무제표 조회 (연도, 분기 내림차순)
    Optional<FinancialStatement> findTopByCompanyOrderByYearDescQuarterDesc(Company company);
    // 과거 재무제표 조회
    List<FinancialStatement> findByCompanyOrderByYearDescQuarterDesc(Company company);
}

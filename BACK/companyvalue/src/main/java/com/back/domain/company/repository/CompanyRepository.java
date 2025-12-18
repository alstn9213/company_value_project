package com.back.domain.company.repository;

import com.back.domain.company.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByTicker(String ticker);
    // 검색 드롭다운
    List<Company> findTop10ByTickerContainingIgnoreCaseOrNameContainingIgnoreCase(String ticker, String name);
    Page<Company> findAll(Pageable pageable);
}


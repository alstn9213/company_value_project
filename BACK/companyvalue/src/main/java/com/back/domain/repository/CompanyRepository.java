package com.back.domain.repository;

import com.back.domain.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByTicker(String ticker);
    List<Company> findByNameContaining(String keyword); // 검색용
    Page<Company> findAll(Pageable pageable);
}


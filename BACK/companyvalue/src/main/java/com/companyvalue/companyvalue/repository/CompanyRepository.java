package com.companyvalue.companyvalue.repository;

import com.companyvalue.companyvalue.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByTicker(String ticker);
    List<Company> findByNameContaining(String keyword); // 검색용
}


package com.back.domain.company.repository;

import com.back.domain.company.entity.Company;
import com.back.domain.company.entity.CompanyScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyScoreRepository extends JpaRepository<CompanyScore, Long> {
    Optional<CompanyScore> findByCompany(Company company);

    // 점수 높은 순으로 조회 (랭킹용)
    List<CompanyScore> findTop10ByOrderByTotalScoreDesc();
}

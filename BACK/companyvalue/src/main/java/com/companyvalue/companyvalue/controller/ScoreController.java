package com.companyvalue.companyvalue.controller;

import com.companyvalue.companyvalue.domain.Company;
import com.companyvalue.companyvalue.domain.repository.CompanyRepository;
import com.companyvalue.companyvalue.domain.repository.CompanyScoreRepository;
import com.companyvalue.companyvalue.dto.MainResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/scores")
@RequiredArgsConstructor
public class ScoreController {

    private final CompanyScoreRepository companyScoreRepository;
    private final CompanyRepository companyRepository;

    // 1. 상위 10개 우량 기업 조회
    @GetMapping("/top")
    public ResponseEntity<List<MainResponseDto.ScoreResult>> getTopRankedCompanies() {
        List<MainResponseDto.ScoreResult> topCompanies = companyScoreRepository.findTop10ByOrderByTotalScoreDesc()
                .stream()
                .map(MainResponseDto.ScoreResult::from)
                .toList();
        return ResponseEntity.ok(topCompanies);
    }

    // 2. 특정 기업 점수 상세 조회
    // GET /api/scores/{ticker}
    @GetMapping("/{ticker}")
    public ResponseEntity<MainResponseDto.ScoreResult> getCompanyScore(@PathVariable String ticker) {
        Company company = companyRepository.findByTicker(ticker)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 기업입니다."));

        return companyScoreRepository.findByCompany(company)
                .map(MainResponseDto.ScoreResult::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

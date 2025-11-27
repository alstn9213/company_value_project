package com.companyvalue.companyvalue.controller;

import com.companyvalue.companyvalue.domain.Company;
import com.companyvalue.companyvalue.domain.CompanyScore;
import com.companyvalue.companyvalue.domain.FinancialStatement;
import com.companyvalue.companyvalue.domain.repository.CompanyRepository;
import com.companyvalue.companyvalue.domain.repository.CompanyScoreRepository;
import com.companyvalue.companyvalue.domain.repository.FinancialStatementRepository;
import com.companyvalue.companyvalue.dto.MainResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyRepository companyRepository;
    private final FinancialStatementRepository financialStatementRepository;
    private final CompanyScoreRepository companyScoreRepository;

    // 1. 전체 기업 목록 조회
    @GetMapping
    public ResponseEntity<Page<MainResponseDto.CompanyInfo>> getAllCompanies(
            @PageableDefault(size = 20, sort = "ticker", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<MainResponseDto.CompanyInfo> companies = companyRepository.findAll(pageable)
                .map(MainResponseDto.CompanyInfo::from);
        return ResponseEntity.ok(companies);
    }

    // 2. 기업 검색(이름으로)
    @GetMapping("/search")
    public ResponseEntity<List<MainResponseDto.CompanyInfo>> searchCompanies(@RequestParam String keyword) {
        List<MainResponseDto.CompanyInfo> result = companyRepository.findByNameContaining(keyword).stream()
                .map(MainResponseDto.CompanyInfo::from)
                .toList();
        return ResponseEntity.ok(result);
    }

    // 3. 기업 상세 정보 조회(기본 정보 + 점수 + 재무제표)
    @GetMapping("/{ticker}")
    public ResponseEntity<MainResponseDto.CompanyDetailResponse> getCompanyDetail(@PathVariable String ticker) {
        Company company = company = companyRepository.findByTicker(ticker)
                .orElseThrow(() -> new IllegalArgumentException("해당 기업을 찾을 수 없습니다. " + ticker));

        // 점수 조회(없으면 null 처리 혹은 기본값)
        CompanyScore score = companyScoreRepository.findByCompany(company)
                .orElse(CompanyScore.builder()
                        .company(company)
                        .totalScore(0)
                        .grade("N/A")
                        .build());

        List<FinancialStatement> history = financialStatementRepository.findByCompanyOrderByYearDescQuarterDesc(company);
        FinancialStatement latest = history.isEmpty() ? new FinancialStatement() : history.get(0);
        List<MainResponseDto.FinancialDetail> historyDto = history.stream()
                .map(MainResponseDto.FinancialDetail::from)
                .toList();

        return ResponseEntity.ok(new MainResponseDto.CompanyDetailResponse(
                MainResponseDto.CompanyInfo.from(company),
                MainResponseDto.ScoreResult.from(score),
                MainResponseDto.FinancialDetail.from(latest),
                historyDto
        ));
    }

}

package com.back.controller;

import com.back.domain.Company;
import com.back.domain.CompanyScore;
import com.back.domain.FinancialStatement;
import com.back.domain.repository.CompanyRepository;
import com.back.domain.repository.CompanyScoreRepository;
import com.back.domain.repository.FinancialStatementRepository;
import com.back.dto.MainResponseDto;
import com.back.dto.StockHistoryDto;
import com.back.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final StockService stockService;

    // 1. 전체 기업 목록 조회
    @GetMapping
    public ResponseEntity<Page<MainResponseDto.CompanyInfo>> getAllCompanies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "name") String sort
    ) {
        // 1. 정렬 기준 설정 (자바 필드명 기준)
        Sort sortObj = Sort.by(Sort.Direction.ASC, "name"); // 기본: 이름순
        if ("score".equals(sort)) {
            // 연관된 객체의 필드로 정렬할 때는 '필드명.필드명' 표기법 사용
            sortObj = Sort.by(Sort.Direction.DESC, "companyScore.totalScore");
        }
        Pageable pageable = PageRequest.of(page, size, sortObj);

        // 2. 조회 및 변환 (Entity -> DTO)
        Page<MainResponseDto.CompanyInfo> companies = companyRepository.findAll(pageable)
                .map(company -> {
                    // 점수 정보가 없는 경우(null) 안전하게 처리
                    int score = (company.getCompanyScore() != null) ? company.getCompanyScore().getTotalScore() : 0;
                    String grade = (company.getCompanyScore() != null) ? company.getCompanyScore().getGrade() : "N/A";

                    return new MainResponseDto.CompanyInfo(
                            company.getTicker(),
                            company.getName(),
                            company.getSector(),
                            company.getExchange(),
                            score,
                            grade
                    );
                });
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

    // 4. 주가 차트 조회
    @GetMapping("/{ticker}/chart")
    public ResponseEntity<List<StockHistoryDto>> getCompanyChart(@PathVariable String ticker) {
        List<StockHistoryDto> chartData = stockService.getStockHistory(ticker);
        return ResponseEntity.ok(chartData);
    }

}

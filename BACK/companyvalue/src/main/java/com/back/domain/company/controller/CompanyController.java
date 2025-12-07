package com.back.domain.company.controller;

import com.back.domain.company.dto.response.*;
import com.back.domain.company.entity.Company;
import com.back.domain.company.entity.CompanyScore;
import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.repository.CompanyRepository;
import com.back.domain.company.repository.CompanyScoreRepository;
import com.back.domain.company.repository.FinancialStatementRepository;
import com.back.domain.company.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Company API", description = "기업 정보 및 재무 데이터 및 주가 차트 조회 API")
public class CompanyController {

    private final CompanyRepository companyRepository;
    private final FinancialStatementRepository financialStatementRepository;
    private final CompanyScoreRepository companyScoreRepository;
    private final StockService stockService;

    // 1. 전체 기업 목록 조회
    @GetMapping
    @Operation(summary = "전체 기업 목록 조회", description = "페이징 처리된 기업 목록을 반환합니다. 이름순 또는 점수순 정렬이 가능합니다.")
    public ResponseEntity<Page<CompanySummaryResponse>> getAllCompanies(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0")
            int page,
            @Parameter(description = "페이지 크기", example = "12")
            @RequestParam(defaultValue = "12")
            int size,
            @Parameter(description = "정렬 기준 (name 또는 score)", example = "score")
            @RequestParam(defaultValue = "name")
            String sort
    ) {
        // 정렬 기준 설정 (자바 필드명 기준)
        Sort sortObj = Sort.by(Sort.Direction.ASC, "name"); // 기본: 이름순
        if ("score".equals(sort)) {
            // 연관된 객체의 필드로 정렬할 때는 '필드명.필드명' 표기법 사용
            sortObj = Sort.by(Sort.Direction.DESC, "companyScore.totalScore");
        }
        Pageable pageable = PageRequest.of(page, size, sortObj);

        // 조회 및 변환 (Entity -> DTO)
        Page<CompanySummaryResponse> companies = companyRepository.findAll(pageable)
                .map(company -> {
                    // 점수 정보가 없는 경우(null) 안전하게 처리
                    int score = (company.getCompanyScore() != null) ? company.getCompanyScore().getTotalScore() : 0;
                    String grade = (company.getCompanyScore() != null) ? company.getCompanyScore().getGrade() : "N/A";

                    return new CompanySummaryResponse(
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
    @Operation(summary = "기업 검색", description = "기업명에 검색어가 포함된 기업 목록을 반환합니다.")
    public ResponseEntity<List<CompanySummaryResponse>> searchCompanies(
            @Parameter(description = "검색할 기업명 또는 키워드", example = "Apple")
            @RequestParam
            String keyword
    ) {
        List<CompanySummaryResponse> result = companyRepository.findByNameContaining(keyword).stream()
                .map(CompanySummaryResponse::from)
                .toList();
        return ResponseEntity.ok(result);
    }

    // 3. 기업 상세 정보 조회(기본 정보 + 점수 + 재무제표)
    @GetMapping("/{ticker}")
    @Operation(summary = "기업 상세 정보 조회", description = "특정 기업의 기본 정보, 재무 건전성 점수, 최근 재무제표 내역을 조회합니다.")
    public ResponseEntity<CompanyDetailResponse> getCompanyDetail(
            @Parameter(description = "기업 티커 (예: AAPL)", example = "AAPL")
            @PathVariable
            String ticker
    ) {
        Company company = companyRepository.findByTicker(ticker)
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
        List<FinancialStatementResponse> historyDto = history.stream()
                .map(FinancialStatementResponse::from)
                .toList();

        return ResponseEntity.ok(new CompanyDetailResponse(
                CompanySummaryResponse.from(company),
                CompanyScoreResponse.from(score),
                FinancialStatementResponse.from(latest),
                historyDto
        ));
    }

    // 4. 주가 차트 조회
    @GetMapping("/{ticker}/chart")
    @Operation(summary = "주가 차트 데이터 조회", description = "특정 기업의 주가 히스토리 데이터를 조회합니다.")
    public ResponseEntity<List<StockHistoryResponse>> getCompanyChart(
            @Parameter(description = "기업 티커 (예: AAPL)", example = "AAPL")
            @PathVariable
            String ticker
    ) {
        List<StockHistoryResponse> chartData = stockService.getStockHistory(ticker);
        return ResponseEntity.ok(chartData);
    }

}

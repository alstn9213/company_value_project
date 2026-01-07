package com.back.domain.company.controller;

import com.back.domain.company.dto.response.*;
import com.back.domain.company.service.CompanyReadService;
import com.back.domain.company.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/companies")
@RequiredArgsConstructor
@Tag(name = "Company API", description = "기업 정보 및 재무 데이터 및 주가 차트 조회 API")
public class CompanyController {
    private final StockService stockService;
    private final CompanyReadService companyReadService;

    @GetMapping
    @Operation(summary = "전체 기업 목록 조회", description = "페이징 처리된 기업 목록을 반환합니다. 이름순 또는 점수순 정렬이 가능합니다.")
    public ResponseEntity<Page<CompanySummaryResponse>> getAllCompanies(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "12")
            @RequestParam(defaultValue = "12") int size,
            @Parameter(description = "정렬 기준 (name 또는 score)", example = "score")
            @RequestParam(defaultValue = "score") String sort
    ) {
        return ResponseEntity.ok(companyReadService.getAllCompanies(page, size, sort));
    }

    @GetMapping("/search")
    @Operation(summary = "기업 검색", description = "기업명에 검색어가 포함된 기업 목록을 반환합니다.")
    public ResponseEntity<List<CompanySummaryResponse>> searchCompanies(
            @Parameter(description = "검색할 기업명 또는 키워드", example = "Apple")
            @RequestParam String keyword
    ) {
        return ResponseEntity.ok(companyReadService.searchCompanies(keyword));
    }

    @GetMapping("/{ticker}")
    @Operation(summary = "기업 상세 정보 조회", description = "특정 기업의 기본 정보, 재무 건전성 점수, 최근 재무제표 내역을 조회합니다.")
    public ResponseEntity<CompanyDetailResponse> getCompanyDetail(
            @Parameter(description = "기업 티커 (예: AAPL)", example = "AAPL")
            @PathVariable String ticker
    ) {
        return ResponseEntity.ok(companyReadService.getCompanyDetail(ticker));
    }

    @GetMapping("/{ticker}/chart")
    @Operation(summary = "주가 차트 데이터 조회", description = "특정 기업의 주가 히스토리 데이터를 조회합니다.")
    public ResponseEntity<List<StockHistoryResponse>> getCompanyChart(
            @Parameter(description = "기업 티커 (예: AAPL)", example = "AAPL")
            @PathVariable String ticker
    ) {
        return ResponseEntity.ok(stockService.getStockHistory(ticker));
    }

}

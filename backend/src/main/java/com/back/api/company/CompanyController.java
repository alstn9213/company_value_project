package com.back.api.company;

import com.back.domain.company.dto.response.*;
import com.back.domain.company.service.CompanyReadService;
import com.back.domain.company.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/companies")
@RequiredArgsConstructor
public class CompanyController implements CompanyControllerDocs {

  private final StockService stockService;
  private final CompanyReadService companyReadService;

  @Override
  @GetMapping
  public ResponseEntity<Page<CompanySummaryResponse>> getAllCompanies(
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "12") int size,
          @RequestParam(defaultValue = "score") String sort
  ) {
    return ResponseEntity.ok(companyReadService.getAllCompanies(page, size, sort));
  }

  @Override
  @GetMapping("/search")
  public ResponseEntity<List<CompanySummaryResponse>> searchCompanies(
          @RequestParam String keyword
  ) {
    return ResponseEntity.ok(companyReadService.searchCompanies(keyword));
  }

  @Override
  @GetMapping("/{ticker}")
  public ResponseEntity<CompanyDetailResponse> getCompanyDetail(
          @PathVariable String ticker
  ) {
    return ResponseEntity.ok(companyReadService.getCompanyDetail(ticker));
  }

  @Override
  @GetMapping("/{ticker}/chart")
  public ResponseEntity<List<StockHistoryResponse>> getCompanyChart(
          @PathVariable String ticker
  ) {
    return ResponseEntity.ok(stockService.getStockHistory(ticker));
  }
}
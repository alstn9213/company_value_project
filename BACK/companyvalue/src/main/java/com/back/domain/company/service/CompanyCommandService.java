package com.back.domain.company.service;

import com.back.domain.company.entity.Company;
import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.entity.StockPriceHistory;
import com.back.domain.company.repository.CompanyRepository;
import com.back.domain.company.repository.FinancialStatementRepository;
import com.back.domain.company.repository.StockPriceHistoryRepository;
import com.back.global.config.init.dto.CompanySeedDto;
import com.back.global.config.init.dto.FinancialSeedDto;
import com.back.global.config.init.dto.StockSeedDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyCommandService {

  private final CompanyRepository companyRepository;
  private final FinancialStatementRepository financialStatementRepository;
  private final StockPriceHistoryRepository stockPriceHistoryRepository;

  @Transactional
  public boolean registerCompany(CompanySeedDto seedData) {
    if (companyRepository.existsByTicker(seedData.ticker())) {
      log.info("이미 존재하는 기업입니다: {}", seedData.ticker());
      return false;
    }

    // 회사 정보 저장
    Company savedCompany = saveCompanyEntity(seedData);

    // --- 연관 데이터 저장 (재무제표, 주가) ---
    saveFinancialStatements(savedCompany, seedData.financials());
    saveStockPriceHistory(savedCompany, seedData.stockHistory());

    log.debug("기업 데이터 등록 완료: {}", seedData.ticker());
    return true;
  }

  // --- 헬퍼 메서드 ---

  // 회사 정보 저장 헬퍼
  private Company saveCompanyEntity(CompanySeedDto seedData) {
    return companyRepository.save(Company.builder()
            .ticker(seedData.ticker())
            .name(seedData.name())
            .sector(seedData.sector())
            .exchange(seedData.exchange())
            .totalShares(seedData.totalShares())
            .build());
  }

  // 재무제표 저장 헬퍼
  private void saveFinancialStatements(Company company, List<FinancialSeedDto> financials) {
    if (financials == null || financials.isEmpty()) {
      log.warn("등록된 재무제표 데이터가 없습니다. (Ticker: {})", company.getTicker());
      return;
    }

    List<FinancialStatement> financialEntities = financials.stream()
            .map(dto -> dto.toEntity(company))
            .toList();

    financialStatementRepository.saveAll(financialEntities);
  }

  // 주가 저장 헬퍼
  private void saveStockPriceHistory(Company company, List<StockSeedDto> stockHistory) {
    if (stockHistory == null || stockHistory.isEmpty()) {
      log.warn("등록된 주가 과거 데이터가 없습니다. (Ticker: {})", company.getTicker());
      return;
    }

    List<StockPriceHistory> stockEntities = stockHistory.stream()
            .map(dto -> dto.toEntity(company))
            .toList();

    stockPriceHistoryRepository.saveAll(stockEntities);
  }


}
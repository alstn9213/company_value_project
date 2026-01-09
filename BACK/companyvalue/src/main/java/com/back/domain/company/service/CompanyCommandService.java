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
import com.back.global.error.ErrorCode;
import com.back.global.error.exception.BusinessException;
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
      // 초기화 로직에서는 예외보다 boolean 반환으로 흐름 제어가 유리함
      log.info("이미 존재하는 기업입니다: {}", seedData.ticker());
      return false;
    }

    // --- 신규 기업 저장 ---
    Company newCompany = seedData.toEntity();
    Company savedCompany = companyRepository.save(newCompany);

    // --- 기업의 재무제표, 주가 저장 ---
    saveFinancialStatements(savedCompany, seedData.financials());
    saveStockPriceHistory(savedCompany, seedData.stockHistory());

    log.debug("기업 데이터 등록 완료: {}", seedData.ticker());
    return true;
  }

  // --- 헬퍼 메서드 ---

  // 재무제표 저장 헬퍼
  private void saveFinancialStatements(Company company, List<FinancialSeedDto> financials) {
    if (financials == null || financials.isEmpty()) {
      log.warn("[데이터 누락] {}: 재무제표", company.getName());
      throw new BusinessException(ErrorCode.REQUIRED_DATA_MISSING);
    }

    List<FinancialStatement> financialEntities = financials.stream()
            .map(dto -> dto.toEntity(company))
            .toList();

    financialStatementRepository.saveAll(financialEntities);
  }

  // 주가 저장 헬퍼
  private void saveStockPriceHistory(Company company, List<StockSeedDto> stockHistory) {
    if (stockHistory == null || stockHistory.isEmpty()) {
      log.warn("[데이터 누락] {}: 주가 기록", company.getName());
      throw new BusinessException(ErrorCode.REQUIRED_DATA_MISSING);
    }

    List<StockPriceHistory> stockEntities = stockHistory.stream()
            .map(dto -> dto.toEntity(company))
            .toList();

    stockPriceHistoryRepository.saveAll(stockEntities);
  }


}
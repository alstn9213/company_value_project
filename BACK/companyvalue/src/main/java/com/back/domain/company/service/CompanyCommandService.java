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
  public void registerCompany(CompanySeedDto seedData) {
    if (companyRepository.existsByTicker(seedData.ticker())) {
      throw new BusinessException(ErrorCode.COMPANY_ALREADY_EXISTS);
    }

    Company newCompany = seedData.toEntity();
    Company savedCompany = companyRepository.save(newCompany);

    // --- 기업의 재무제표, 주가 저장 ---
    saveFinancialStatements(savedCompany, seedData.financials());
    saveStockPriceHistory(savedCompany, seedData.stockHistory());

    log.debug("기업 데이터 등록 완료: {}", seedData.ticker());
  }

  // --- 헬퍼 메서드 ---

  // 재무제표 저장 헬퍼
  private void saveFinancialStatements(Company company, List<FinancialSeedDto> financials) {
    if (financials == null || financials.isEmpty()) {
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
      throw new BusinessException(ErrorCode.REQUIRED_DATA_MISSING);
    }

    List<StockPriceHistory> stockEntities = stockHistory.stream()
            .map(dto -> dto.toEntity(company))
            .toList();

    stockPriceHistoryRepository.saveAll(stockEntities);
  }


}
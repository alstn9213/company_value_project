package com.back.domain.company.service;

import com.back.domain.company.entity.Company;
import com.back.domain.company.repository.CompanyRepository;
import com.back.domain.company.repository.FinancialStatementRepository;
import com.back.domain.company.repository.StockPriceHistoryRepository;
import com.back.global.config.init.dto.CompanySeedDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyCommandService {

  private final CompanyRepository companyRepository;
  private final FinancialStatementRepository financialStatementRepository;
  private final StockPriceHistoryRepository stockPriceHistoryRepository;
  
  public boolean registerCompany(CompanySeedDto seedData) {
    if (companyRepository.existsByTicker(seedData.ticker())) {
      return false;
    }

    // 회사 정보 저장
    Company company = companyRepository.save(Company.builder()
            .ticker(seedData.ticker())
            .name(seedData.name())
            .sector(seedData.sector())
            .exchange(seedData.exchange())
            .totalShares(seedData.totalShares())
            .build());

    // 재무제표 저장
    if (seedData.financials() != null && !seedData.financials().isEmpty()) {
      financialStatementRepository.saveAll(
              seedData.financials().stream()
                      .map(dto -> dto.toEntity(company))
                      .toList()
      );
    }

    // 주가 데이터 저장
    if (seedData.stockHistory() != null && !seedData.stockHistory().isEmpty()) {
      stockPriceHistoryRepository.saveAll(
              seedData.stockHistory().stream()
                      .map(dto -> dto.toEntity(company))
                      .toList()
      );
    }

    log.debug("기업 데이터 등록 완료: {}", seedData.ticker());
    return true;
  }
}
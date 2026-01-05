package com.back.global.config.init;

import com.back.domain.company.entity.Company;
import com.back.domain.company.entity.StockPriceHistory;
import com.back.domain.company.repository.CompanyRepository;
import com.back.domain.company.repository.FinancialStatementRepository;
import com.back.domain.company.repository.StockPriceHistoryRepository;
import com.back.domain.company.service.analysis.ScoringService;
import com.back.global.config.init.dto.CompanySeedDto;
import com.back.global.config.init.dto.StockSeedDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CompanyDataInitializer {

  private final CompanyRepository companyRepository;
  private final FinancialStatementRepository financialStatementRepository;
  private final StockPriceHistoryRepository stockPriceHistoryRepository;

  private final SeedDataLoader seedDataLoader;
  private final ScoringService scoringService;

  @Transactional
  public void initCompanyData() {
    log.info("[CompanyDataInitializer] 데이터 초기화를 시작합니다...");

    log.info("기존 데이터 삭제 중...");
    stockPriceHistoryRepository.deleteAll();     // 주가 데이터 삭제
    financialStatementRepository.deleteAll();    // 재무제표 데이터 삭제
    companyRepository.deleteAll();               // 기업 정보 삭제

    log.info("새로운 시드 데이터 로드 중...");
    List<CompanySeedDto> seedDataList = seedDataLoader.loadSeedData();
    if(seedDataList.isEmpty()) {
      log.warn("시드 데이터가 비어있습니다. 초기화를 종료합니다.");
      return;
    }

    for(CompanySeedDto seedData : seedDataList) {
      Company company = companyRepository.save(Company.builder()
        .ticker(seedData.ticker())
        .name(seedData.name())
        .sector(seedData.sector())
        .exchange(seedData.exchange())
        .totalShares(seedData.totalShares())
        .build());

    if(seedData.financials() != null && !seedData.financials().isEmpty()) {
      financialStatementRepository.saveAll(
        seedData.financials().stream()
                .map(dto -> seedDataLoader.mapToFinancialEntity(company, dto))
                .toList()
      );
    }

    if(seedData.stockHistory() != null && !seedData.stockHistory().isEmpty()) {
      stockPriceHistoryRepository.saveAll(
        seedData.stockHistory().stream()
                .map(dto -> seedDataLoader.mapToStockEntity(company, dto))
                .toList()
        );
      }
    }

    log.info("[CompanyDataInitializer] 데이터 적재 완료. 점수 산출 시작...");

    scoringService.calculateAllScores();

    log.info("[CompanyDataInitializer] 초기화 최종 완료. (총 {}개 기업)", seedDataList.size());
  }
}

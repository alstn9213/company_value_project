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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    List<CompanySeedDto> seedDataList = seedDataLoader.loadSeedData();
    if (seedDataList.isEmpty()) {
      log.warn("시드 데이터가 비어있습니다. 초기화를 종료합니다.");
      return;
    }

    Set<String> processedTickers = new HashSet<>();
    int newCompanyCount = 0;

    for(CompanySeedDto seedData : seedDataList) {
      String ticker = seedData.ticker();
      if(processedTickers.contains(ticker)) continue;
      processedTickers.add(seedData.ticker());
      if(companyRepository.existsByTicker(ticker)) continue;

      try {
        // 회사 정보 저장
        Company company = companyRepository.save(Company.builder()
                .ticker(ticker)
                .name(seedData.name())
                .sector(seedData.sector())
                .exchange(seedData.exchange())
                .totalShares(seedData.totalShares())
                .build());

        // 재무제표 저장
        if (seedData.financials() != null && !seedData.financials().isEmpty()) {
          financialStatementRepository.saveAll(
                  seedData.financials().stream()
                          .map(dto -> seedDataLoader.mapToFinancialEntity(company, dto))
                          .toList()
          );
        }

        // 주가 데이터 저장
        if (seedData.stockHistory() != null && !seedData.stockHistory().isEmpty()) {
          stockPriceHistoryRepository.saveAll(
                  seedData.stockHistory().stream()
                          .map(dto -> seedDataLoader.mapToStockEntity(company, dto))
                          .toList()
          );
        }
        newCompanyCount++;

      } catch (Exception e) {
        // 특정 기업 저장 실패가 전체 초기화 로직을 중단시키지 않도록 예외 처리
        log.error("기업 데이터 저장 중 오류 발생 (Ticker: {}): {}", ticker, e.getMessage());
      }
    }

    log.info("[CompanyDataInitializer] 데이터 적재 완료. 점수 산출 시작...");

    // 점수 산출은 데이터가 변경되었을 때만 수행하거나, 별도 스케줄러로 분리하는 것이 좋으나
    // 초기화 단계에서는 필요하다면 수행합니다.
    if (newCompanyCount > 0) {
      log.info("[CompanyDataInitializer] 신규 데이터가 있어 점수 재산출을 진행합니다.");
      scoringService.calculateAllScores();
    } else {
      log.info("[CompanyDataInitializer] 신규 데이터가 없어 점수 산출을 생략합니다.");
    }

    log.info("[CompanyDataInitializer] 초기화 최종 완료.");
  }
}

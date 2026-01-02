package com.back.global.config.init;

import com.back.domain.company.entity.Company;
import com.back.domain.company.repository.CompanyRepository;
import com.back.domain.company.repository.FinancialStatementRepository;
import com.back.domain.company.repository.StockPriceHistoryRepository;
import com.back.domain.company.service.analysis.ScoringService;
import com.back.global.config.init.dto.CompanySeedDto;
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
        if(companyRepository.count() > 0) {
            log.info("[CompanyDataInitializer] 데이터가 이미 존재하여 초기화를 건너뜁니다.");
            return;
        }

        log.info("[CompanyDataInitializer] 시드 데이터를 로드합니다...");

        List<CompanySeedDto> seedDataList = seedDataLoader.loadSeedData();
        for(CompanySeedDto seedData : seedDataList) {
            Company company = companyRepository.save(Company.builder()
                    .ticker(seedData.ticker())
                    .name(seedData.name())
                    .sector(seedData.sector())
                    .exchange(seedData.exchange())
                    .build());

            if(seedData.financials() != null) {
                financialStatementRepository.saveAll(
                        seedData.financials().stream()
                                .map(dto -> seedDataLoader.mapToFinancialEntity(company, dto))
                                .toList()
                );
            }

            if(seedData.stockHistory() != null) {
                stockPriceHistoryRepository.saveAll(
                        seedData.stockHistory().stream()
                                .map(dto -> seedDataLoader.mapToStockEntity(company, dto))
                                .toList()
                );
            }
        }

        log.info("[CompanyDataInitializer] 데이터 적재 완료. 기업 점수 산출을 시작합니다...");

        scoringService.calculateAllScores();

        log.info("[CompanyDataInitializer] 초기화 완료. {}개 기업 등록됨.", seedDataList.size());
    }





}

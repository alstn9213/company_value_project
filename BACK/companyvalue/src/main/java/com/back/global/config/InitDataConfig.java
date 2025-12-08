package com.back.global.config;

import com.back.domain.company.entity.Company;
import com.back.domain.company.entity.CompanyScore;
import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.entity.StockPriceHistory;
import com.back.domain.company.repository.CompanyRepository;
import com.back.domain.company.repository.CompanyScoreRepository;
import com.back.domain.company.repository.FinancialStatementRepository;
import com.back.domain.company.repository.StockPriceHistoryRepository;
import com.back.domain.company.service.analysis.ScoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class InitDataConfig {

    private final CompanyRepository companyRepository;
    private final StockPriceHistoryRepository stockRepository;
    private final FinancialStatementRepository financialRepository;
    private final CompanyScoreRepository scoreRepository;
    private final ScoringService scoringService;

    private final Random random = new Random();

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            log.info("[InitData] 기업 데이터 초기화 작업을 시작합니다...");

            List<Company> companies = List.of(
                    Company.builder().ticker("AAPL").name("Apple Inc.").sector("Technology").exchange("NASDAQ").build(),
                    createCompany("ZOMB", "Zombie Tech", "Technology", "NASDAQ"),
                    createCompany("DEBT", "Heavy Debt Corp", "Industrial", "NYSE"),
                    createCompany("BEST", "Diamond Holdings", "Financial Services", "NYSE"),
                    createCompany("GROW", "Hyper Growth", "Technology", "NASDAQ"),
                    createCompany("SAMS", "Samsung Fake", "Technology", "KRX"),
                    createCompany("HYUN", "Hyundai Fake", "Consumer Cyclical", "KRX"),
                    createCompany("NAVR", "Naver Fake", "Communication Services", "KRX"),
                    createCompany("KAKA", "Kakao Fake", "Communication Services", "KRX"),
                    createCompany("POSC", "Posco Fake", "Basic Materials", "KRX"),
                    createCompany("KBANK", "KB Financial", "Financial Services", "KRX"),
                    createCompany("SHIN", "Shinhan Group", "Financial Services", "KRX"),
                    createCompany("SKHY", "SK Hynix Fake", "Technology", "KRX"),
                    createCompany("LGEN", "LG Energy Fake", "Industrial", "KRX"),
                    createCompany("CELL", "Celltrion Fake", "Healthcare", "KRX"),
                    createCompany("COUP", "Coupang Fake", "Consumer Cyclical", "NYSE")
            );

            for (Company company : companies) {
                if(companyRepository.findByTicker(company.getTicker()).isEmpty()) {
                    Company savedCompany = companyRepository.save(company);
                    log.info("[InitData] 생성 완료: {}", savedCompany.getName());
                }
            }
            log.info("[InitData] 모든 데이터 초기화 완료.");
        };
    }

    // --- 내부 메서드 ---

    private Company createCompany(String ticker, String name, String sector, String exchange) {
        return Company.builder()
                .ticker(ticker)
                .name(name)
                .sector(sector)
                .exchange(exchange)
                .build();
    }
    
}

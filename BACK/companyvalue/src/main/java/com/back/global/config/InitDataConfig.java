package com.back.global.config;

import com.back.domain.company.entity.Company;

import com.back.domain.company.repository.CompanyRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class InitDataConfig {

    private final CompanyRepository companyRepository;


    @Bean
    public CommandLineRunner initData() {
        return args -> {
            log.info("[InitData] 가상의 미국 기업 데이터 생성을 시작합니다...");


            List<Company> companies = List.of(
                    // api로 불러오는 애플 정보
                    Company.builder().ticker("AAPL").name("Apple Inc.").sector("Technology").exchange("NASDAQ").build(),
                    // 가상의 미국 기업 20개 리스트 (NASDAQ, NYSE)
                    // 1. Technology (기술주)
                    createCompany("NXAI", "NextGen AI Solutions", "Technology", "NASDAQ"),
                    createCompany("QBIT", "Quantum Bit Computing", "Technology", "NASDAQ"),
                    createCompany("CYBR", "Cyber Shield Systems", "Technology", "NASDAQ"),
                    createCompany("CLOUD", "Sky High Cloud Services", "Technology", "NASDAQ"),
                    createCompany("DATA", "Big Data Analytics", "Technology", "NASDAQ"),
                    createCompany("VIRT", "Virtual Reality Corp", "Technology", "NASDAQ"),

                    // 2. Financial Services (금융주)
                    createCompany("LBNK", "Liberty National Bank", "Financial Services", "NYSE"),
                    createCompany("GFIN", "Global Finance Group", "Financial Services", "NYSE"),
                    createCompany("WALTH", "Wealth Management Partners", "Financial Services", "NYSE"),
                    createCompany("SURE", "Surety Insurance Co", "Financial Services", "NYSE"),

                    // 3. Healthcare (헬스케어)
                    createCompany("MEDI", "MediCare Plus Solutions", "Healthcare", "NYSE"),
                    createCompany("BIOX", "BioGenix Labs", "Healthcare", "NASDAQ"),
                    createCompany("NANO", "Nano Cure Technologies", "Healthcare", "NASDAQ"),

                    // 4. Consumer Cyclical (임의소비재 - 자동차, 사치품 등)
                    createCompany("EVMT", "Future EV Motors", "Consumer Cyclical", "NASDAQ"),
                    createCompany("LUXE", "Luxury Brands Holdings", "Consumer Cyclical", "NYSE"),

                    // 5. Consumer Defensive (필수소비재 - 음식료 등)
                    createCompany("FOOD", "Organic Whole Foods", "Consumer Defensive", "NYSE"),
                    createCompany("DRNK", "Global Beverage Inc.", "Consumer Defensive", "NYSE"),

                    // 6. Industrial (산업재)
                    createCompany("AERO", "AeroSpace Dynamics", "Industrial", "NYSE"),
                    createCompany("ROBO", "Robotics Automation", "Industrial", "NASDAQ"),

                    // 7. Basic Materials (원자재)
                    createCompany("CHEM", "Advanced Chemical Works", "Basic Materials", "NYSE")
            );

            for(Company company : companies) {
                Company savedCompany = companyRepository.findByTicker(company.getTicker())
                                .orElseGet(() -> {
                                    Company newCompany = companyRepository.save(company);
                                    log.info("[InitData] 기업 생성: {}", newCompany.getName());
                                    return newCompany;
                                });
            }
            log.info("[InitData] 모든 데이터 초기화 완료.");
        };
    }


    // --- 유틸 메서드 ---
    private Company createCompany(String ticker, String name, String sector, String exchange) {
        return Company.builder()
                .ticker(ticker)
                .name(name)
                .sector(sector)
                .exchange(exchange)
                .build();
    }
}

package com.back.global.config.init;

import com.back.domain.company.entity.Company;
import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.entity.StockPriceHistory;
import com.back.domain.company.repository.CompanyRepository;
import com.back.domain.company.service.analysis.ScoringService;
import com.back.global.config.init.dto.CompanySeedDto;
import com.back.global.config.init.dto.FinancialSeedDto;
import com.back.global.config.init.dto.StockSeedDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!test") // 테스트 환경이 아닐 때만 실행
public class SeedDataLoader implements CommandLineRunner {

  private final CompanyRepository companyRepository;
  private final ScoringService scoringService;
  private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (companyRepository.count() > 0) {
            log.info("이미 데이터가 존재하여 초기화를 건너뜁니다.");
            return;
        }

        log.info("Seed Data 로딩 시작...");

        // 1. JSON 파일 읽기 및 DB 저장 (기존 로직 유지)
        // ... (JSON 파싱 및 엔티티 저장 로직) ...

        log.info("Seed Data 저장 완료. 기업 점수 계산을 시작합니다.");

        // 2. [중요] 저장된 데이터를 기반으로 점수 계산 트리거
        // 기존 FinancialDataSyncService에서 하던 역할을 여기서 수행
        scoringService.calculateAllScores();

        log.info("초기 데이터 세팅 및 점수 계산 완료!");
    }


    // JSON 파일 로드 및 파싱
    public List<CompanySeedDto> loadSeedData() {
        try {
            ClassPathResource resource = new ClassPathResource("data/seed_data.json");
            if(!resource.exists()) {
                log.warn("시드 데이터 파일이 없습니다. (src/main/resources/data/seed_data.json)");
                return Collections.emptyList();
            }
            InputStream inputStream = resource.getInputStream();
            return objectMapper.readValue(inputStream, new TypeReference<List<CompanySeedDto>>() {});
        } catch (Exception e) {
            log.error("시드 데이터 로딩 실패", e);
            throw new RuntimeException("초기 데이터 로딩 중 오류 발생");
        }
    }

    // --- 헬퍼 메서드 (DTO -> Entity 변환) ---

    public FinancialStatement mapToFinancialEntity(Company company, FinancialSeedDto dto) {
        return FinancialStatement.builder()
                .company(company)
                .year(dto.year())
                .quarter(dto.quarter())
                .revenue(dto.revenue())
                .operatingProfit(dto.operatingProfit())
                .netIncome(dto.netIncome())
                .totalAssets(dto.totalAssets())
                .totalLiabilities(dto.totalLiabilities())
                .totalEquity(dto.totalEquity())
                .operatingCashFlow(dto.operatingCashFlow())
                .researchAndDevelopment(dto.researchAndDevelopment())
                .capitalExpenditure(dto.capitalExpenditure())
                .build();
    }

    public StockPriceHistory mapToStockEntity(Company company, StockSeedDto dto) {
        return StockPriceHistory.builder()
                .company(company)
                .recordedDate(LocalDate.parse(dto.date()))
                .closePrice(dto.closePrice())
                .build();
    }

}

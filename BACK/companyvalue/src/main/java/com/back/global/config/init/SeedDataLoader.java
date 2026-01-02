package com.back.global.config.init;

import com.back.domain.company.entity.Company;
import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.entity.StockPriceHistory;
import com.back.global.config.init.dto.CompanySeedDto;
import com.back.global.config.init.dto.FinancialSeedDto;
import com.back.global.config.init.dto.StockSeedDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeedDataLoader{

  private final ObjectMapper objectMapper;


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

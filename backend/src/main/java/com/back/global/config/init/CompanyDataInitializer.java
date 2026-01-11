package com.back.global.config.init;

import com.back.domain.company.service.CompanyCommandService;
import com.back.domain.valuation.engine.ScoringService;
import com.back.global.config.init.dto.CompanySeedDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class CompanyDataInitializer {

  private final CompanyCommandService companyCommandService;
  private final SeedDataLoader seedDataLoader;
  private final ScoringService scoringService;

  public void initCompanyData() {
    log.info("[CompanyDataInitializer] 데이터 초기화를 시작합니다...");

    List<CompanySeedDto> seedDataList = seedDataLoader.loadSeedData();

    if (seedDataList.isEmpty()) {
      log.warn("시드 데이터가 비어있습니다. 초기화를 종료합니다.");
      return;
    }

    int newCompanyCount = processSeedData(seedDataList);
    handlePostInitialization(newCompanyCount);
  }

  // --- 헬퍼 메서드 ---

  // 신규 기업 저장하는 헬퍼
  private int processSeedData(List<CompanySeedDto> seedDataList) {
    Set<String> processedTickers = new HashSet<>();
    int count = 0;

    for (CompanySeedDto seedData : seedDataList) {
      String ticker = seedData.ticker();

      if (processedTickers.contains(seedData.ticker())) {
        log.info("이미 저장된 기업입니다.");
        continue;
      }

      processedTickers.add(ticker);

      try {
        boolean isCreated = companyCommandService.registerCompany(seedData);
        if (isCreated) count++;

      } catch (Exception e) {
        log.error("[데이터 저장 실패] {}: {}", seedData.ticker(), e.getMessage());
      }
    }

    return count;
  }

  // 신규 기업이 저장될 경우 점수 산출하는 헬퍼
  private void handlePostInitialization(int newCompanyCount) {
    log.info("[CompanyDataInitializer] 데이터 적재 완료: 신규 {}건", newCompanyCount);
    if (newCompanyCount > 0) {
      log.info("[신규 데이터 감지] 전체 점수 재산출을 진행합니다.");
      scoringService.calculateScoresAndSave();
    }
  }


}

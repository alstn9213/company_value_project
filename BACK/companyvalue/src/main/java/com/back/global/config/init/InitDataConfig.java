package com.back.global.config.init;

import com.back.domain.macro.service.MacroDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class InitDataConfig {

  private final MacroDataService macroDataService;
  private final CompanyDataInitializer companyDataInitializer;

  @Bean
  public CommandLineRunner initData() {
    return args -> {
      log.info("[InitData] 데이터 초기화 작업을 시작합니다...");
      macroDataService.initHistoricalMacroData();
      companyDataInitializer.initCompanyData();
      log.info("[InitData] 데이터 초기화 로직 완료.");
    };
  }



}

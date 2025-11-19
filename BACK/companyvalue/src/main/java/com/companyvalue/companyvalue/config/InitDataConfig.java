package com.companyvalue.companyvalue.config;

import com.companyvalue.companyvalue.domain.Company;
import com.companyvalue.companyvalue.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class InitDataConfig {

    private final CompanyRepository companyRepository;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // 1. DB에 데이터가 이미 있는지 확인 (중복 추가 방지)
            if (companyRepository.count() > 0) {
                log.info("[InitData] 이미 데이터가 존재하여 초기화를 건너뜁니다.");
                return;
            }

            log.info("[InitData] 초기 기업 데이터를 생성합니다...");

            // 2. 테스트용 기업 데이터 생성 (Builder 패턴 활용)
            Company apple = Company.builder()
                    .ticker("AAPL")
                    .name("Apple Inc.")
                    .sector("Technology")
                    .exchange("NASDAQ")
                    .build();

            Company tesla = Company.builder()
                    .ticker("TSLA")
                    .name("Tesla Inc.")
                    .sector("Consumer Cyclical")
                    .exchange("NASDAQ")
                    .build();

            // 3. DB 저장
            companyRepository.save(apple);
            companyRepository.save(tesla);

            log.info("[InitData] 초기 데이터 생성 완료: AAPL, TSLA");
        };
    }
}
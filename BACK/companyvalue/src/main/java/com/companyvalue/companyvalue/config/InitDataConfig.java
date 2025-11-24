package com.companyvalue.companyvalue.config;

import com.companyvalue.companyvalue.domain.Company;
import com.companyvalue.companyvalue.domain.repository.CompanyRepository;
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
            log.info("[InitData] 기업 데이터 초기화 작업을 시작합니다...");

            // 추가하고 싶은 10개+ 기업 리스트 정의
            List<Company> targetCompanies = List.of(
                    Company.builder().ticker("AAPL").name("Apple Inc.").sector("Technology").exchange("NASDAQ").build(),
                    Company.builder().ticker("TSLA").name("Tesla Inc.").sector("Consumer Cyclical").exchange("NASDAQ").build(),
                    Company.builder().ticker("MSFT").name("Microsoft Corp.").sector("Technology").exchange("NASDAQ").build(),
                    Company.builder().ticker("GOOGL").name("Alphabet Inc.").sector("Technology").exchange("NASDAQ").build(),
                    Company.builder().ticker("AMZN").name("Amazon.com Inc.").sector("Consumer Cyclical").exchange("NASDAQ").build(),
                    Company.builder().ticker("NVDA").name("NVIDIA Corp.").sector("Technology").exchange("NASDAQ").build(),
                    Company.builder().ticker("META").name("Meta Platforms Inc.").sector("Technology").exchange("NASDAQ").build(),
                    Company.builder().ticker("BRK.B").name("Berkshire Hathaway").sector("Financial Services").exchange("NYSE").build(),
                    Company.builder().ticker("JPM").name("JPMorgan Chase & Co.").sector("Financial Services").exchange("NYSE").build(),
                    Company.builder().ticker("V").name("Visa Inc.").sector("Financial Services").exchange("NYSE").build(),
                    Company.builder().ticker("JNJ").name("Johnson & Johnson").sector("Healthcare").exchange("NYSE").build(),
                    Company.builder().ticker("WMT").name("Walmart Inc.").sector("Consumer Defensive").exchange("NYSE").build()
            );


            for (Company company : targetCompanies) {
                // DB에 해당 티커가 없을 때만 저장 (중복 방지)
                if (companyRepository.findByTicker(company.getTicker()).isEmpty()) {
                    companyRepository.save(company);
                    log.info("[InitData] 새 기업 추가: {} ({})", company.getTicker(), company.getName());
                }
            }

            log.info("[InitData] 기업 목록 확인 완료.");
        };
    }
}
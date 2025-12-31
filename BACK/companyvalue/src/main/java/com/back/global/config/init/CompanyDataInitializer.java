package com.back.global.config.init;

import com.back.domain.company.entity.Company;
import com.back.domain.company.repository.CompanyRepository;
import com.back.domain.company.repository.FinancialStatementRepository;
import com.back.domain.company.repository.StockPriceHistoryRepository;
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
    private final DummyDataGenerator dummyDataGenerator;

    @Transactional
    public void initCompanyData() {
        log.info("[CompanyDataInitializer] 기업 데이터 확인 및 생성을 시작합니다...");
        // --- 나중에 api로 호출할 기업 리스트를 먼저 DB에 적재한다. ---
        List<Company> companies = dummyDataGenerator.createCompanyList();
        for(Company companyData: companies) {
            Company company = getOrSaveCompany(companyData); // AAPL과 더미 회사들을 DB에 저장
            if(shouldGenerateDummyData(company)) generateAndSaveDummyData(company); // 더미 재무정보, 주가 데이터 생성
        }
    }


    private Company getOrSaveCompany(Company companyData) {
        return companyRepository.findByTicker(companyData.getTicker())
                .orElseGet(() -> {
                    log.info("[CompanyDataInitializer] 신규 기업 등록: {}", companyData.getName());
                    return companyRepository.save(companyData);
                });
    }

    // AAPL이 아닌 더미 회사이고 재무 데이터가 없다면 생성하는 헬퍼 메서드
    private boolean shouldGenerateDummyData(Company company) {
        return !"AAPL".equals(company.getTicker())
                && !financialStatementRepository.existsByCompany(company);
    }

    // 더미 회사의 데이터를 생성하는 헬퍼메서드
    private void generateAndSaveDummyData(Company company) {
        log.info("[CompanyDataInitializer] {} - 더미 재무/주가 데이터 생성 중...", company.getName());

        // Generator에게 데이터 '생성'만 요청
        var financials = dummyDataGenerator.generateFinancials(company);
        var stockHistory = dummyDataGenerator.generateStockPrices(company, financials);

        // 여기서는 '저장'만 담당
        financialStatementRepository.saveAll(financials);
        stockPriceHistoryRepository.saveAll(stockHistory);
    }
}

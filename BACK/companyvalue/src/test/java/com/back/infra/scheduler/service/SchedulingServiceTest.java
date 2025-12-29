package com.back.infra.scheduler.service;

import com.back.domain.company.entity.Company;
import com.back.domain.company.repository.CompanyRepository;
import com.back.domain.company.repository.CompanyScoreRepository;
import com.back.domain.company.repository.FinancialStatementRepository;
import com.back.domain.company.service.finance.FinancialDataSyncService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class SchedulingServiceTest {

    @Autowired
    private CompanyBatchService companyBatchService;

    @MockitoBean
    private CompanyRepository companyRepository;

    @MockitoBean
    private FinancialStatementRepository financialStatementRepository;

    @MockitoBean
    private CompanyScoreRepository companyScoreRepository;

    @MockitoBean
    private FinancialDataSyncService financialDataSyncService;

    @Test
    @DisplayName("배치 실행 시 최신 데이터가 없는 기업에 대해 동기화 메서드가 호출되어야 한다")
    void executeAllCompaniesUpdate_success() {
        // --- given ---
        Company apple = Company.builder().ticker("AAPL").name("Apple").build();
        given(companyRepository.findAll()).willReturn(List.of(apple));

        // 데이터가 최신이 아니라고 가정 (재무제표 없음)
        given(financialStatementRepository.findTopByCompanyOrderByYearDescQuarterDesc(apple))
                .willReturn(Optional.empty());
        // 점수도 없다고 가정
        given(companyScoreRepository.findByCompany(apple))
                .willReturn(Optional.empty());

        // --- when ---
        companyBatchService.executeAllCompaniesUpdate();

        // --- then ---
        // BatchService는 SyncService에게 동기화를 위임했는지 확인
        verify(financialDataSyncService, times(1)).synchronizeCompany("AAPL");
    }
}
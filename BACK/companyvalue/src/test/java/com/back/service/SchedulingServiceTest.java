package com.back.service;

import com.back.domain.company.entity.Company;
import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.repository.CompanyRepository;
import com.back.domain.company.repository.FinancialStatementRepository;
import com.back.domain.company.service.finance.FinancialDataSyncService;
import com.back.domain.company.service.finance.FinancialStatementService;
import com.back.infra.external.dto.ExternalFinancialDataResponse;
import com.back.domain.company.service.analysis.ScoringService;
import com.back.infra.external.DataFetchService;
import com.back.infra.scheduler.SchedulingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class SchedulingServiceTest {

    @Autowired
    private SchedulingService schedulingService;

    @MockitoBean
    private CompanyRepository companyRepository;

    @MockitoBean
    private FinancialStatementRepository financialStatementRepository;

    @MockitoBean
    private FinancialDataSyncService financialDataSyncService;

    @MockitoBean
    private DataFetchService dataFetchService;

    @MockitoBean
    private ScoringService scoringService;

    @Test
    @DisplayName("스케줄러 실행 시 데이터 동기화와 점수 계산이 순차적으로 수행되어야 한다")
    void executeAllCompaniesUpdate_success() {
        // given
        Company apple = Company.builder().ticker("AAPL").name("Apple").build();
        given(companyRepository.findAll()).willReturn(List.of(apple));

        // 1. SyncService가 호출된 이후, 점수 계산을 위해 재무제표를 조회할 때 리턴될 객체 Mocking
        FinancialStatement mockFs = new FinancialStatement();
        given(financialStatementRepository.findTopByCompanyOrderByYearDescQuarterDesc(apple))
                .willReturn(Optional.of(mockFs));

        // 2. 점수 계산 시 필요한 Overview 데이터 Mocking
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode overview = mapper.createObjectNode();
        given(dataFetchService.getCompanyOverview("AAPL")).willReturn(overview);

        // when
        schedulingService.executeAllCompaniesUpdate();

        // then
        // 1. [핵심] 기존 fetchRawFinancialData 대신 SyncService의 메서드가 호출되었는지 검증
        verify(financialDataSyncService, times(1)).synchronizeCompany("AAPL");

        // 2. 점수 계산 서비스가 호출되었는지 검증
        verify(scoringService, times(1)).calculateAndSaveScore(any(FinancialStatement.class), any());
    }
}
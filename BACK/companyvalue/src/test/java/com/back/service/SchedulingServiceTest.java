package com.back.service;

import com.back.domain.company.entity.Company;
import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.repository.CompanyRepository;
import com.back.domain.company.repository.FinancialStatementRepository;
import com.back.infra.external.dto.ExternalFinancialDataResponse;
import com.back.domain.company.service.FinancialDataService;
import com.back.domain.company.service.ScoringService;
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
    private FinancialDataService financialDataService;

    @MockitoBean
    private DataFetchService dataFetchService;

    @MockitoBean
    private ScoringService scoringService;

    @Test
    @DisplayName("재무제표 업데이트 스케줄러: 데이터가 없는 기업에 대해 데이터를 수집하고 점수를 계산해야 한다")
    void executeAllCompaniesUpdate_success() {
        // given
        Company apple = Company.builder().ticker("AAPL").name("Apple").build();
        given(companyRepository.findAll()).willReturn(List.of(apple));

        given(financialStatementRepository.findTopByCompanyOrderByYearDescQuarterDesc(apple))
                .willReturn(Optional.empty())
                .willReturn(Optional.of(new FinancialStatement()));

        ExternalFinancialDataResponse mockData = new ExternalFinancialDataResponse(null, null, null);
        given(financialDataService.fetchRawFinancialData("AAPL")).willReturn(mockData);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode overview = mapper.createObjectNode();
        given(dataFetchService.getCompanyOverview("AAPL")).willReturn(overview);

        // when
        schedulingService.executeAllCompaniesUpdate();

        // then
        verify(financialDataService, times(1)).fetchRawFinancialData("AAPL");
        verify(financialDataService, times(1)).saveFinancialData(eq("AAPL"), any());
        verify(scoringService, times(1)).calculateAndSaveScore(any(), any());
    }
}
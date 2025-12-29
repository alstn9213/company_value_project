package com.back.domain.company.service.finance;

import com.back.domain.company.entity.Company;
import com.back.domain.company.event.CompanyFinancialsUpdatedEvent;
import com.back.domain.company.repository.CompanyRepository;
import com.back.domain.company.service.stock.StockPriceImportService;
import com.back.infra.external.DataFetchService;
import com.back.infra.external.ExternalFinancialDataResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class FinancialDataSyncServiceTest {

    @InjectMocks
    private FinancialDataSyncService financialDataSyncService;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private DataFetchService dataFetchService;

    @Mock
    private FinancialStatementService financialStatementService;

    @Mock
    private StockPriceImportService stockPriceImportService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private ExternalFinancialDataResponse mockFinancialResponse;

    @Test
    @DisplayName("동기화 성공 시 재무제표를 저장하고 점수 계산을 요청해야 한다")
    void synchronizeCompany_success() {
        // --- given ---
        String ticker = "AAPL";
        Company company = Company.builder()
                .ticker(ticker)
                .build();

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode mockStockData = mapper.createObjectNode();
        mockStockData.put("Time Series (Daily)", "some data"); // 내용이 비어있지 않음

        // 회사 조회 Mock
        given(companyRepository.findByTicker(ticker))
                .willReturn(Optional.of(company));

        // 재무 데이터 API 호출 Mock
        given(dataFetchService.getCombinedFinancialData(ticker))
                .willReturn(mockFinancialResponse);
        given(mockFinancialResponse.hasAllData())
                .willReturn(true); // 필수 데이터가 있다고 가정

        // 주가 데이터 API 호출 Mock
        given(dataFetchService.getDailyStockHistory(ticker))
                .willReturn(mockStockData);

        // --- when ---
        financialDataSyncService.synchronizeCompany(ticker);

        // --- then ---
        // 재무제표 저장 서비스가 호출되었는지 검증
        verify(financialStatementService, times(1))
                .saveFinancialStatements(company, mockFinancialResponse);

        // 주가 저장 서비스가 호출되었는지 검증
        verify(stockPriceImportService, times(1))
                .fetchAndSaveStockHistory(company, mockStockData);

        // 점수 계산 트리거를 위한 이벤트가 발행되었는지 검증
        verify(eventPublisher, times(1))
                .publishEvent(any(CompanyFinancialsUpdatedEvent.class));

    }
}

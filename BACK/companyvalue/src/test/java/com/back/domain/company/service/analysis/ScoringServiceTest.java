package com.back.domain.company.service.analysis;

import com.back.domain.company.entity.Company;
import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.entity.StockPriceHistory;
import com.back.domain.company.repository.StockPriceHistoryRepository;
import com.back.domain.company.service.analysis.dto.ScoringData;
import com.back.domain.company.service.analysis.policy.PenaltyPolicy;
import com.back.domain.company.service.analysis.strategy.InvestmentStrategy;
import com.back.domain.company.service.analysis.strategy.ProfitabilityStrategy;
import com.back.domain.company.service.analysis.strategy.StabilityStrategy;
import com.back.domain.company.service.analysis.strategy.ValuationStrategy;
import com.back.domain.macro.entity.MacroEconomicData;
import com.back.domain.company.repository.CompanyRepository;
import com.back.domain.company.repository.CompanyScoreRepository;
import com.back.domain.macro.repository.MacroRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScoringServiceTest {

    @InjectMocks
    private ScoringService scoringService;

    @Mock
    private CompanyScoreRepository companyScoreRepository;
    @Mock
    private MacroRepository macroRepository;
    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private StockPriceHistoryRepository stockPriceHistoryRepository;

    // 전략 패턴 적용에 따른 Mock 추가
    @Mock
    private StabilityStrategy stabilityStrategy;
    @Mock
    private ProfitabilityStrategy profitabilityStrategy;
    @Mock
    private ValuationStrategy valuationStrategy;
    @Mock
    private InvestmentStrategy investmentStrategy;
    @Mock
    private PenaltyPolicy penaltyPolicy;

    @Test
    @DisplayName("정상 흐름: 모든 전략이 점수를 반환하면 합산되어 저장되어야 한다")
    void calculateScore_normal_success() {
        // --- given ---
        Company company = Company.builder().ticker("AAPL").sector("Technology").build();

        FinancialStatement fs = FinancialStatement.builder()
                .company(company)
                .totalEquity(BigDecimal.valueOf(100)) // 자본 잠식 아님
                .build();

        // 거시 경제: 정상 (금리 역전 없음)
        MacroEconomicData macro = MacroEconomicData.builder().build();
        StockPriceHistory history = StockPriceHistory.builder()
                .company(company)
                .closePrice(BigDecimal.valueOf(150))
                .build();

        // Overview 데이터 (PER, PBR)
        ObjectNode overview = new ObjectMapper().createObjectNode();

        // Repository 동작 정의
        when(macroRepository.findTopByOrderByRecordedDateDesc()).thenReturn(Optional.of(macro));
        when(stockPriceHistoryRepository.findTopByCompanyOrderByRecordedDateDesc(company)).thenReturn(history);
        when(companyScoreRepository.findByCompany(company)).thenReturn(Optional.empty());

        // 전략 동작 정의 (각 25점씩 리턴한다고 가정 -> 총점 100점)
        when(stabilityStrategy.calculate(any(ScoringData.class))).thenReturn(25);
        when(profitabilityStrategy.calculate(any(ScoringData.class))).thenReturn(25);
        when(valuationStrategy.calculate(any(ScoringData.class))).thenReturn(25);
        when(investmentStrategy.calculate(any(ScoringData.class))).thenReturn(25);

        // 페널티 동작 정의 (페널티 없음)
        when(penaltyPolicy.calculatePenalty(any(), any())).thenReturn(0);

        // --- when ---
        scoringService.calculateAndSaveScore(fs, overview);

        // --- then ---
        // verify를 통해 save 메서드가 호출되었는지,
        // 그리고 저장되는 객체의 값이 예상대로인지 검증
        verify(companyScoreRepository, times(1)).save(argThat(score ->
                score.getTotalScore() == 100 && "S".equals(score.getGrade())
        ));
    }

    @Test
    @DisplayName("페널티 적용: 페널티가 발생하면 총점에서 차감되어야 한다")
    void calculateScore_withPenalty() {
        // --- given ---
        Company company = Company.builder()
                .ticker("TEST")
                .build();

        FinancialStatement fs = FinancialStatement.builder()
                .company(company)
                .totalEquity(BigDecimal.TEN)
                .build();

        MacroEconomicData macro = MacroEconomicData.builder().build();

        StockPriceHistory history = StockPriceHistory.builder()
                .closePrice(BigDecimal.TEN)
                .build();

        when(macroRepository.findTopByOrderByRecordedDateDesc()).thenReturn(Optional.of(macro));
        when(stockPriceHistoryRepository.findTopByCompanyOrderByRecordedDateDesc(company)).thenReturn(history);
        when(companyScoreRepository.findByCompany(company)).thenReturn(Optional.empty());

        // 전략 점수 (총 80점)
        when(stabilityStrategy.calculate(any())).thenReturn(20);
        when(profitabilityStrategy.calculate(any())).thenReturn(20);
        when(valuationStrategy.calculate(any())).thenReturn(20);
        when(investmentStrategy.calculate(any())).thenReturn(20);

        // 페널티 30점 부여
        when(penaltyPolicy.calculatePenalty(any(), any())).thenReturn(30);

        // --- when ---
        scoringService.calculateAndSaveScore(fs, null);

        // --- then ---
        // 80 - 30 = 50점 저장 확인
        verify(companyScoreRepository).save(argThat(score ->
                score.getTotalScore() == 50
        ));
    }

}

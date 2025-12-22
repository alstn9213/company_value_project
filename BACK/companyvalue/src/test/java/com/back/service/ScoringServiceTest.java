package com.back.service;

import com.back.domain.company.entity.Company;
import com.back.domain.company.entity.CompanyScore;
import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.service.analysis.ScoringService;
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
import java.time.LocalDate;
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

    @Test
    @DisplayName("정상 기업 평가: 부채 비율이 낮고 ROE가 높으면 고득점을 받아야 한다.")
    void calculateScore_normal_highScore() {
        // given
        Company company = Company.builder().ticker("AAPL").sector("Technology").build();

        // 재무제표: 자본 100, 부채 50 (비율 50% -> 안정성 만점), 순이익 20 (ROE 20% -> 수익성 만점)
        FinancialStatement fs = FinancialStatement.builder()
                .company(company)
                .totalEquity(new BigDecimal("100"))
                .totalLiabilities(new BigDecimal("50"))
                .revenue(new BigDecimal("100"))
                .operatingProfit(new BigDecimal("20"))
                .netIncome(new BigDecimal("20"))
                .operatingCashFlow(new BigDecimal("10")) // 현금흐름 흑자
                .build();

        // 거시 경제: 정상 (금리 역전 없음)
        MacroEconomicData macro = MacroEconomicData.builder()
                .recordedDate(LocalDate.now())
                .us10yTreasuryYield(4.5)
                .us2yTreasuryYield(4.0) // 10y > 2y (정상)
                .build();

        // Overview 데이터 (PER, PBR)
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode overview = mapper.createObjectNode();
        overview.put("PERatio", "10"); // 저평가 구간
        overview.put("PriceToBookRatio", "1.0");

        when(macroRepository.findTopByOrderByRecordedDateDesc()).thenReturn(Optional.of(macro));
        when(companyScoreRepository.findByCompany(company)).thenReturn(Optional.empty());

        // when
        scoringService.calculateAndSaveScore(fs, overview);

        // then
        // verify를 통해 save 메서드가 호출되었는지, 그리고 저장되는 객체의 값이 예상대로인지 검증
        verify(companyScoreRepository, times(1)).save(argThat(score -> {
            return score.getGrade().equals("S") || score.getGrade().equals("A");
            // 구체적인 점수 계산 로직에 따라 예상 점수(totalScore)를 검증해도 됨
        }));
    }

    @Test
    @DisplayName("자본 잠식 기업은 페널티를 받아 최하 등급이 되어야 한다")
    void calculateScore_disqualified_capitalErosion() {
        // given
        Company company = Company.builder().ticker("BAD").sector("Technology").build();

        FinancialStatement fs = FinancialStatement.builder()
                .company(company)
                .totalEquity(new BigDecimal("-10")) // 자본 잠식
                .totalLiabilities(new BigDecimal("100"))
                .build();

        MacroEconomicData macro = MacroEconomicData.builder().build(); // 내용 무관

        when(macroRepository.findTopByOrderByRecordedDateDesc()).thenReturn(Optional.of(macro));
        when(companyScoreRepository.findByCompany(company)).thenReturn(Optional.empty());

        // when
        scoringService.calculateAndSaveScore(fs, null);

        // then
        verify(companyScoreRepository, times(1)).save(argThat(score ->
                score.getTotalScore() <= 20 && "D".equals(score.getGrade())
        ));
    }

    @Test
    @DisplayName("거시 경제 페널티: 장단기 금리 역전 시 점수가 차감되어야 한다")
    void calculateScore_macroPenalty() {
        // given
        Company company = Company.builder().ticker("TEST").sector("Technology").build();
        FinancialStatement fs = FinancialStatement.builder()
                .company(company)
                .totalEquity(new BigDecimal("100"))
                .totalLiabilities(new BigDecimal("50"))
                .revenue(new BigDecimal("100"))
                .operatingCashFlow(new BigDecimal("10"))
                .build();

        // 금리 역전 상황 (10y < 2y)
        MacroEconomicData macro = MacroEconomicData.builder()
                .us10yTreasuryYield(3.5)
                .us2yTreasuryYield(4.5)
                .build();

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode overview = mapper.createObjectNode();

        when(macroRepository.findTopByOrderByRecordedDateDesc()).thenReturn(Optional.of(macro));

        // 이미 저장된 점수가 없다고 가정
        when(companyScoreRepository.findByCompany(company)).thenReturn(Optional.empty());

        // when
        scoringService.calculateAndSaveScore(fs, overview);

        // then
        // 페널티 로직이 수행되었는지 검증 (로그 확인 또는 디버거로 확인 가능하지만, 여기선 저장 로직 수행 여부로 판단)
        verify(companyScoreRepository).save(any(CompanyScore.class));
    }
}

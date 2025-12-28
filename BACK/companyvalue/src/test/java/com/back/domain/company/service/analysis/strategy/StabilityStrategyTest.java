package com.back.domain.company.service.analysis.strategy;


import com.back.domain.company.entity.Company;
import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.service.analysis.dto.ScoringData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class StabilityStrategyTest {

    @InjectMocks
    private StabilityStrategy stabilityStrategy;

    @Mock
    private FinancialStatement fs;

    @Mock
    private Company company;

    @Test
    @DisplayName("일반 기업: 부채비율 100% 미만이고 현금흐름이 양수면 만점(40점)이다")
    void normal_company_max_score() {
        // given
        setupBaseData("Technology", BigDecimal.valueOf(100)); // 영업현금흐름 100 (양수)

        // 자본 100, 부채 50 -> 부채비율 50%
        given(fs.getTotalEquity())
                .willReturn(BigDecimal.valueOf(100));
        given(fs.getTotalLiabilities())
                .willReturn(BigDecimal.valueOf(50));

        ScoringData data = new ScoringData(fs, null, null);

        // when
        int score = stabilityStrategy.calculate(data);

        // then
        assertThat(score).isEqualTo(40);
    }

    @Test
    @DisplayName("일반 기업: 부채비율이 200% 이상 300% 미만이면 5점을 받는다")
    void normal_company_high_debt() {
        // given
        setupBaseData("Manufacturing", BigDecimal.ZERO); // 영업현금흐름 0

        // 자본 100, 부채 250 -> 부채비율 250%
        given(fs.getTotalEquity())
                .willReturn(BigDecimal.valueOf(100));
        given(fs.getTotalLiabilities())
                .willReturn(BigDecimal.valueOf(250));

        ScoringData data = new ScoringData(fs, null, null);

        // when
        int score = stabilityStrategy.calculate(data);

        // then
        assertThat(score).isEqualTo(5);
    }

    @Test
    @DisplayName("금융업(Financial Services): 부채비율이 800% 미만이면 부채 점수 만점(20점)이다")
    void finance_company_debt_score() {
        // given
        // 금융업은 부채비율 기준이 관대함 (800% 미만일 때 20점)
        setupBaseData("Financial Services", BigDecimal.ZERO); // 현금흐름 점수 제외하고 부채 점수만 확인

        // 자본 100, 부채 700 -> 부채비율 700%
        given(fs.getTotalEquity())
                .willReturn(BigDecimal.valueOf(100));
        given(fs.getTotalLiabilities())
                .willReturn(BigDecimal.valueOf(700));

        ScoringData data = new ScoringData(fs, null, null);

        // when
        int score = stabilityStrategy.calculate(data);

        // then
        assertThat(score).isEqualTo(20);
    }

    @Test
    @DisplayName("자본 잠식(자본 <= 0) 상태면 부채 점수는 0점이다")
    void capital_erosion_zero_score() {
        // given
        // 자본이 0 이하면 부채비율 계산 로직을 건너뜀
        given(fs.getTotalEquity())
                .willReturn(BigDecimal.valueOf(-10));
        given(fs.getTotalLiabilities())
                .willReturn(BigDecimal.valueOf(100));

        // 현금흐름은 흑자로 설정해봄 (총점 20점 예상)
        given(fs.getOperatingCashFlow())
                .willReturn(BigDecimal.valueOf(50));

        ScoringData data = new ScoringData(fs, null, null);

        // when
        int score = stabilityStrategy.calculate(data);

        // then
        assertThat(score).isEqualTo(20); // 부채 점수 0 + 현금흐름 20
    }

    // --- 기본 헬퍼 메서드 ---
    // 반복되는 Mock 설정 줄이기 위한 헬퍼 메서드
    private void setupBaseData(String sector, BigDecimal operatingCashFlow) {
        // 1. 회사 섹터 설정 (금융업 여부 판단용)
        given(fs.getCompany()).willReturn(company);
        given(company.getSector()).willReturn(sector);

        // 2. 영업활동 현금흐름 설정
        given(fs.getOperatingCashFlow()).willReturn(operatingCashFlow);
    }



}

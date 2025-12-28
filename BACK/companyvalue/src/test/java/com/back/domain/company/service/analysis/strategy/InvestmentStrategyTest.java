package com.back.domain.company.service.analysis.strategy;

import com.back.domain.company.entity.Company;
import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.service.analysis.dto.ScoringData;
import com.back.global.error.ErrorCode;
import com.back.global.error.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
public class InvestmentStrategyTest {

    @InjectMocks
    private InvestmentStrategy investmentStrategy;

    @Mock
    private FinancialStatement fs;

    @Mock
    private Company company;

    @Test
    @DisplayName("투자 비율이 15% 이상이면 10점을 반환한다")
    void return_10_when_ratio_is_over_15() {
        // given
        setupFinancialData(BigDecimal.valueOf(1000), BigDecimal.valueOf(100), BigDecimal.valueOf(50));
        ScoringData data = new ScoringData(fs,null, null);

        // when
        int score = investmentStrategy.calculate(data);

        // then
        assertThat(score).isEqualTo(10);
    }

    @Test
    @DisplayName("투자 비율이 10% 이상 15% 미만이면 7점을 반환한다")
    void return_7_when_ratio_is_between_10_and_15() {
        // given
        // 매출 1000, R&D 50, Capex 50 -> 합계 100 (10%)
        setupFinancialData(BigDecimal.valueOf(1000), BigDecimal.valueOf(50), BigDecimal.valueOf(50));
        ScoringData data = new ScoringData(fs, null, null);

        // when
        int score = investmentStrategy.calculate(data);

        // then
        assertThat(score).isEqualTo(7);
    }

    @Test
    @DisplayName("투자 비율이 5% 이상 10% 미만이면 3점을 반환한다")
    void return_3_when_ratio_is_between_5_and_10() {
        // given
        // 매출 1000, R&D 25, Capex 25 -> 합계 50 (5%)
        setupFinancialData(BigDecimal.valueOf(1000), BigDecimal.valueOf(25), BigDecimal.valueOf(25));
        ScoringData data = new ScoringData(fs, null, null);

        // when
        int score = investmentStrategy.calculate(data);

        // then
        assertThat(score).isEqualTo(3);
    }

    @Test
    @DisplayName("투자 비율이 5% 미만이면 0점을 반환한다")
    void return_0_when_ratio_is_under_5() {
        // given
        // 매출 1000, R&D 20, Capex 20 -> 합계 40 (4%)
        setupFinancialData(BigDecimal.valueOf(1000), BigDecimal.valueOf(20), BigDecimal.valueOf(20));
        ScoringData data = new ScoringData(fs, null, null);

        // when
        int score = investmentStrategy.calculate(data);

        // then
        assertThat(score).isEqualTo(0);
    }

    @Test
    @DisplayName("R&D 데이터가 null이면 예외가 발생한다")
    void throw_exception_when_rnd_is_null() {
        // given
        given(fs.getRevenue()).willReturn(BigDecimal.valueOf(1000));
        given(fs.getResearchAndDevelopment()).willReturn(null);

        // 로깅을 위해 Company 이름 조회 모킹
        given(fs.getCompany()).willReturn(company);
        given(company.getName()).willReturn("Test Company");

        ScoringData data = new ScoringData(fs, null, null);

        // when & then
        assertThatThrownBy(() -> investmentStrategy.calculate(data))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INSUFFICIENT_DATA_FOR_SCORING);
    }

    @Test
    @DisplayName("매출액이 0이면 0으로 나누기 오류 없이 0점을 반환한다")
    void return_0_when_revenue_is_zero() {
        // given
        given(fs.getRevenue())
                .willReturn(BigDecimal.ZERO);
        // R&D null 체크를 통과하기 위해 값 설정 (매출 0 체크는 그 뒤에 일어날 수도 있고 앞일 수도 있지만 로직상 R&D 체크가 먼저임)
        given(fs.getResearchAndDevelopment())
                .willReturn(BigDecimal.valueOf(10));

        ScoringData data = new ScoringData(fs, null, null);

        // when
        int score = investmentStrategy.calculate(data);

        // then
        assertThat(score).isEqualTo(0);
    }

    // --- 기본 헬퍼 메서드 ---
    // 테스트 데이터를 구성하는 헬퍼 메서드
    private void setupFinancialData(BigDecimal revenue, BigDecimal rnd, BigDecimal capex) {
        given(fs.getRevenue()).willReturn(revenue);
        given(fs.getResearchAndDevelopment()).willReturn(rnd);
        given(fs.getCapitalExpenditure()).willReturn(capex);
    }
}

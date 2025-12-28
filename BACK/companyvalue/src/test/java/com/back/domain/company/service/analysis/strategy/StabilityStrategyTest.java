package com.back.domain.company.service.analysis.strategy;


import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.service.analysis.dto.ScoringData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;


public class StabilityStrategyTest {

    private final StabilityStrategy stabilityStrategy = new StabilityStrategy();

    @Test
    @DisplayName("부채비율이 50% 미만이면 안정성 점수 만점(25점)을 받아야 한다.")
    void calculate_lowDebtRatio_maxScore() {
        // --- given ---
        // 자본 100, 부채 40 -> 부채비율 40%
        FinancialStatement fs = FinancialStatement.builder()
                .totalEquity(BigDecimal.valueOf(100))
                .totalLiabilities(BigDecimal.valueOf(40))
                .build();

        ScoringData data = new ScoringData(fs, null, BigDecimal.ZERO);

        // --- when ---
        int score = stabilityStrategy.calculate(data);

        // --- then ---
        assertThat(score).isEqualTo(25);
    }

    @Test
    @DisplayName("자본 잠식 상태면 0점을 반환해야 한다.")
    void calculate_capitalErosion_zeroScore() {
        // --- given ---
        FinancialStatement fs = FinancialStatement.builder()
                .totalEquity(BigDecimal.valueOf(-10)) // 자본 잠식
                .totalLiabilities(BigDecimal.valueOf(100))
                .build();

        ScoringData data = new ScoringData(fs, null, BigDecimal.ZERO);

        // --- when ---
        int score = stabilityStrategy.calculate(data);

        // --- then ---
        assertThat(score).isEqualTo(0);
    }
}

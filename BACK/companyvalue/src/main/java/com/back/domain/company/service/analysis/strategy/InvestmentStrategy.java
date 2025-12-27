package com.back.domain.company.service.analysis.strategy;

import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.service.analysis.dto.ScoringData;
import com.back.global.error.ErrorCode;
import com.back.global.error.exception.BusinessException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Component
public class InvestmentStrategy implements ScoringStrategy {

    @Override
    public int calculate(ScoringData data) {
        FinancialStatement fs = data.fs();
        BigDecimal revenue = data.fs().getRevenue();

        if(fs.getResearchAndDevelopment() == null) {
            log.error("{}의 R&D 데이터를 찾을 수 없습니다.", fs.getCompany().getName());
            throw new BusinessException(ErrorCode.INSUFFICIENT_DATA_FOR_SCORING);
        }

        if(revenue.compareTo(BigDecimal.ZERO) == 0) return 0;

        BigDecimal rnd = fs.getResearchAndDevelopment();
        BigDecimal capex = fs.getCapitalExpenditure(); // 자본 지출은 설비들을 구입하니 미래를 위한 투자로 분류
        BigDecimal investmentSum = rnd.add(capex);

        double ratio = investmentSum.divide(revenue, 4, RoundingMode.HALF_UP).doubleValue() * 100;

        if(ratio >= 15) return 10;
        else if(ratio >= 10) return 7;
        else if(ratio >= 5) return 3;

        return 0;
    }
}

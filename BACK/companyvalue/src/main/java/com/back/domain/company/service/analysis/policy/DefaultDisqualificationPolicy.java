package com.back.domain.company.service.analysis.policy;

import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.service.analysis.constant.ScoringConstants;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class DefaultDisqualificationPolicy implements DisqualificationPolicy{

    private static final BigDecimal MAX_DEBT_RATIO = new BigDecimal("400");

    @Override
    public boolean isDisqualified(FinancialStatement fs) {
        BigDecimal assets = fs.getTotalAssets();
        BigDecimal equity = fs.getTotalEquity();
        BigDecimal liabilities = fs.getTotalLiabilities();

        // 자본 잠식 체크
        if(equity.compareTo(BigDecimal.ZERO) <= 0) return true;

        // 2. 부채 비율 확인 (Liabilities / Equity * 100 > 400%)
        if (equity.compareTo(BigDecimal.ZERO) == 0) {
            // 자본이 0인데 부채가 있으면 부채비율 무한대 -> 실격
            if (liabilities.compareTo(BigDecimal.ZERO) > 0) {
                return true;
            }
            // 자본도 0이고 부채도 0이면 (데이터 없음) -> 통과시킴 (점수는 낮게 나오겠지만 실격은 아님)
            return false;
        }

        // 안전한 나눗셈 (RoundingMode 필수)
        BigDecimal debtRatio = liabilities.divide(equity, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));

        return debtRatio.compareTo(MAX_DEBT_RATIO) > 0;
    }
}
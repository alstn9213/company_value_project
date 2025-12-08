package com.back.domain.company.service.analysis.policy;

import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.service.analysis.constant.ScoringConstants;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class DefaultDisqualificationPolicy implements DisqualificationPolicy{

    @Override
    public boolean isDisqualified(FinancialStatement fs) {
        BigDecimal equity = fs.getTotalEquity();
        BigDecimal liabilities = fs.getTotalLiabilities();

        // 자본 잠식 체크
        if(equity.compareTo(BigDecimal.ZERO) <= 0) return true;

        return isDebtRatioExceeded(fs, liabilities, equity);
    }

    private boolean isDebtRatioExceeded(FinancialStatement fs, BigDecimal liabilities, BigDecimal equity) {
        BigDecimal debtRatio = liabilities.divide(equity, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        boolean isFinance = ScoringConstants.SECTOR_FINANCIAL.equalsIgnoreCase(fs.getCompany().getSector());
        double limit = isFinance
                ? ScoringConstants.DEBT_RATIO_LIMIT_FINANCIAL
                : ScoringConstants.DEBT_RATIO_LIMIT_GENERAL;

        return debtRatio.doubleValue() > limit;
    }
}
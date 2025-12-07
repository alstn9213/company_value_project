package com.back.domain.company.service.analysis.policy;

import com.back.domain.company.entity.FinancialStatement;

public interface DisqualificationPolicy {
    boolean isDisqualified(FinancialStatement fs);
}

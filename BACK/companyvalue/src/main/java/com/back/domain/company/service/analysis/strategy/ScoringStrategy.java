package com.back.domain.company.service.analysis.strategy;

import com.back.domain.company.service.analysis.dto.ScoringData;

public interface ScoringStrategy {
    int calculate(ScoringData data);
}

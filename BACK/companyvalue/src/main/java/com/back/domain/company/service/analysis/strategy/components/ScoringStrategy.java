package com.back.domain.company.service.analysis.strategy.components;

import com.back.domain.company.service.analysis.constant.ScoreCategory;
import com.back.domain.company.service.analysis.dto.ScoringData;

public interface ScoringStrategy {
  int calculate(ScoringData data);
  ScoreCategory getCategory();
}

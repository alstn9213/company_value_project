package com.back.domain.company.service.analysis.strategy.components;

import com.back.domain.company.service.analysis.constant.ScoreCategory;
import com.back.domain.company.service.analysis.dto.ScoringDataDto;

public interface ScoringStrategy {
  int calculate(ScoringDataDto data);
  ScoreCategory getCategory();
}

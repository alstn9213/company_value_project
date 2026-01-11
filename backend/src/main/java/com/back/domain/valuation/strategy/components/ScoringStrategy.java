package com.back.domain.valuation.strategy.components;


import com.back.domain.valuation.constant.ScoreCategory;
import com.back.domain.valuation.model.ScoringDataDto;

public interface ScoringStrategy {
  int calculate(ScoringDataDto data);
  ScoreCategory getCategory();
}

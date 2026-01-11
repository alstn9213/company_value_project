package com.back.domain.company.service.analysis.strategy;

import com.back.domain.company.service.analysis.constant.ScoreCategory;
import com.back.domain.company.service.analysis.dto.ScoringDataDto;
import com.back.domain.company.service.analysis.strategy.components.ScoringStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ScoringAggregator {

  private final List<ScoringStrategy> strategies;

  public Map<ScoreCategory, Integer> calculateAll(ScoringDataDto data) {
    Map<ScoreCategory, Integer> scores = new EnumMap<>(ScoreCategory.class);

    for (ScoringStrategy strategy : strategies) {
      int score = strategy.calculate(data);
      scores.put(strategy.getCategory(), score);
    }
    return scores;
  }
}

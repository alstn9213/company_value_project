package com.back.domain.company.service.analysis;

import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.service.analysis.constant.ScoreCategory;
import com.back.domain.company.service.analysis.dto.ScoreEvaluationResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

import static com.back.domain.company.service.analysis.constant.ScoringConstants.*;

@Component
public class ScoreEvaluator {

  public ScoreEvaluationResult evaluate(Map<ScoreCategory, Integer> componentScores, int penalty, FinancialStatement fs) {
    // 각 영역 점수 추출
    int stability = componentScores.getOrDefault(ScoreCategory.STABILITY, 0);
    int profitability = componentScores.getOrDefault(ScoreCategory.PROFITABILITY, 0);
    int valuation = componentScores.getOrDefault(ScoreCategory.VALUATION, 0);
    int investment = componentScores.getOrDefault(ScoreCategory.INVESTMENT, 0);

    // 기본 점수 및 총점 계산
    int baseScore = stability + profitability + valuation + investment;
    int totalScore = Math.max(0, Math.min(100, baseScore - penalty));

    // 등급 산정
    String grade = calculateGrade(totalScore);

    // 기회 여부 판단 (자본잠식이 없고, 밸류에이션 점수가 높을 때)
    boolean isOpportunity = (valuation >= OPPORTUNITY_VALUATION_THRESHOLD)
            && (fs.getTotalEquity().compareTo(BigDecimal.ZERO) > 0);

    return new ScoreEvaluationResult(
            totalScore, stability, profitability, valuation, investment, grade, isOpportunity
    );
  }

  // --- 헬퍼 메서드 ---

  private String calculateGrade(int score) {
    if (score >= GRADE_S_THRESHOLD) return "S";
    if (score >= GRADE_A_THRESHOLD) return "A";
    if (score >= GRADE_B_THRESHOLD) return "B";
    if (score >= GRADE_C_THRESHOLD) return "C";
    return "D";
  }
}

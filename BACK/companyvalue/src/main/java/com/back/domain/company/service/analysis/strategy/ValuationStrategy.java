package com.back.domain.company.service.analysis.strategy;

import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.service.analysis.dto.ScoringData;
import com.back.global.error.ErrorCode;
import com.back.global.error.exception.BusinessException;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.core.util.Json;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class ValuationStrategy implements ScoringStrategy {

  @Override
  public int calculate(ScoringData data) {
    JsonNode overview = data.overview();

    if (!isValidOverview(overview)) {
      log.warn("Valuation 데이터 누락: {}", data.fs().getCompany().getName());
      return 0;
    }

    double per = parseDouble(overview, "PERRatio");
    double pbr = parseDouble(overview, "PriceToBookRatio");

    // per이나 pbr이 0이면 기업 상태가 최악이므로 0점
    if (per == 0 || pbr == 0) return 0;

    return calculateScore(per, pbr);
  }



  // --- 헬퍼 메서드 ---

  // 점수 계산 헬퍼
  private int calculateScore(double per, double pbr) {
    int score = 0;

    // PER 평가
    if (per > 0 && per < 15) score += 10;
    else if (per >= 15 && per < 25) score += 7;
    else if (per >= 25 && per < 40) score += 3;

    // PBR 평가
    if (pbr > 0 && pbr < 1.5) score += 10;
    else if (pbr >= 1.5 && pbr < 3.0) score += 7;
    else if (pbr >= 3.0 && pbr < 5.0) score += 3;

    return score;
  }

  // JSON 필드 파싱 헬퍼
  private double parseDouble(JsonNode node, String field) {
    // 적자 기업이나 자본 잠식일 경우 PBR이나 PER이 None으로 표시된다.
    if (node.has(field) && !node.get(field).asText().equalsIgnoreCase("None")) {
      try {
        return Double.parseDouble(node.get(field).asText());
      } catch (NumberFormatException e) {
        return 0.0;
      }
    }
    return 0.0;
  }

  // Overview 유효성 체크 헬퍼
  private boolean isValidOverview(JsonNode overview) {
    return overview != null && overview.has("PERRatio");
  }



}

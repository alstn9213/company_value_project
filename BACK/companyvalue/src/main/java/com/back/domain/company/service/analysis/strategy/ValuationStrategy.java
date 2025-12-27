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

    private static final double DUMMY_SHARES = 100_000_000.0; // 더미 발행 주식 수 (1억 주)

    @Override
    public int calculate(ScoringData data) {
        JsonNode overview = data.overview();
        BigDecimal latestStockPrice = data.latestStockPrice();
        FinancialStatement fs = data.fs();

        if(!isValidOverview(overview) && !isValidStockPrice(latestStockPrice)) {
            log.error("{}의 재무제표나 최신 주가 데이터가 없습니다.", fs.getCompany().getName());
            throw new BusinessException(ErrorCode.INSUFFICIENT_DATA_FOR_SCORING);
        }

        double per = 0.0;
        double pbr = 0.0;

        if(isValidOverview(overview)) {
            // 외부 API의 pbr, per
            per = parseDouble(overview, "PERatio");
            pbr = parseDouble(overview, "PriceToBookRatio");
        } else {
            // 더미 데이터의 pbr, per 계산
            per = calculateDummyPer(data);
            pbr = calculateDummyPbr(data);
        }

        // per이나 pbr이 0이면 기업 상태가 최악이므로 0점
        if(per == 0 || pbr == 0) return 0;

        return calculateScore(per, pbr);
    }

    // --- 내부 메서드 ---

    // 점수 계산 내부 메서드
    private int calculateScore(double per, double pbr) {
        int score = 0;

        // PER 평가
        if(0 < per && per < 15) score += 10;
        else if(15 <= per && per < 25) score += 7;
        else if(25 <= per && per < 40) score += 3;
        // PBR 평가
        if(0 < pbr && pbr < 1.5) score += 10;
        else if(1.5 <= pbr && pbr < 3.0) score += 7;
        else if(3.0 <= pbr && pbr < 5.0) score += 3;

        return score;
    }

    // 재무제표 JsonNode의 특정 field를 Double로 변경하는 내부 메서드
    private double parseDouble(JsonNode node, String field) {
        // 적자 기업이나 자본 잠식일 경우 PBR이나 PER이 None으로 표시된다.
        if(node.has(field) && !node.get(field).asText().equalsIgnoreCase("None")) {
            try {
                return Double.parseDouble(node.get(field).asText());
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return 0.0;
    }

    // 재무제표 JsonNode가 있는지 확인하는 내부 메서드
    private boolean isValidOverview(JsonNode overview) {
        return overview != null && overview.has("PERatio");
    }

    // 최신 주가가 있는지 확인하는 내부 메서드
    private boolean isValidStockPrice(BigDecimal price) {
        return price != null && price.compareTo(BigDecimal.ZERO) > 0;
    }

    // 더미 데이터 PER 계산 내부 메서드
    private double calculateDummyPer(ScoringData data) {
        FinancialStatement fs = data.fs();
        double stockPrice = data.latestStockPrice().doubleValue();
        // 연간 순이익 추정 = 분기 순이익 * 4
        double annualNetIncome = fs.getNetIncome().doubleValue() * 4;
        // EPS(주당 순이익) = 연간 순이익 / 발행 주식 수
        double eps = annualNetIncome / DUMMY_SHARES;
        // PER = 주가 / EPS
        if(eps > 0) return stockPrice / eps;
        return 0.0;
    }

    // 더미 데이터 PBR 계산 내부 메서드
    private double calculateDummyPbr(ScoringData data) {
        FinancialStatement fs = data.fs();
        double stockPrice = data.latestStockPrice().doubleValue();
        // BPS(주당 순자산) = 자본 총계 / 발행 주식 수
        double bps = fs.getTotalEquity().doubleValue() / DUMMY_SHARES;
        // PBR = 주가 / BPS
        if(bps > 0) return stockPrice / bps;
        return 0.0;
    }


}

package com.companyvalue.companyvalue.service;

import com.companyvalue.companyvalue.domain.FinancialStatement;
import com.companyvalue.companyvalue.domain.MacroEconomicData;
import com.companyvalue.companyvalue.repository.CompanyScoreRepository;
import com.companyvalue.companyvalue.repository.MacroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ScoringService {

    private final CompanyScoreRepository companyScoreRepository;
    private final MacroRepository macroRepository;

    /**
     * 기업의 재무제표와 현재 시장 상황을 기반으로 점수를 계산하고 저장합니다.
     */
    @Transactional
    public void calculateAndSaveScore(FinancialStatement fs) {
        // 1. 최신 거시 경제 지표 가져오기
        MacroEconomicData macro = macroRepository.findTopByOrderByRecordedDateDesc()
                .orElseThrow(() -> new RuntimeException("거시 경제 데이터가 없습니다."));

        // 2. 각 항목별 점수 계산
        int stability = calculateStability(fs);
        int profitability = calculateProfitability(fs);
        int valuation = calculateValuation(fs); // 현재 주가 정보 필요(여기서는 생략하거나 fs에 포함 가정)
        int investment = calculateInvestment(fs); // 가산점 (+10)

        // 3. 페널티 적용 및 총점 계산
        int totalScore = stability + profitability + valuation + investment;

        // 3-1. 과락 체크 (하나라도 걸리면 0점)
        if (isDisqualified(fs)) {
            totalScore = 0;
        } else {
            // 3-2. 동적 페널티 적용
            totalScore -= applyMacroPenalty(macro); // 시장 상황 페널티 (-10)
            totalScore -= applyRiskyInvestmentPenalty(fs, macro); // 고금리 위험 투자 (-15)
        }

        // 4. 점수 보정 (0 ~ 100 범위 유지)
        totalScore = Math.max(0, Math.min(100, totalScore));

        // 5. 결과 저장 (Entity 생성 및 save 로직 생략 - Builder 패턴 사용 권장)
        // CompanyScore score = CompanyScore.builder()...build();
        // companyScoreRepository.save(score);
    }

    // --- 세부 로직 메서드 ---

    private int calculateStability(FinancialStatement fs) {
        // 부채비율, 유동비율 등을 계산하여 40점 만점 기준 점수 반환
        // 예: 부채비율 100% 미만이면 만점, 200% 초과면 0점 등
        return 30; // (임시 반환값)
    }

    private int calculateProfitability(FinancialStatement fs) {
        // ROE, 영업이익률 계산 (30점 만점)
        return 20; // (임시 반환값)
    }

    private int calculateValuation(FinancialStatement fs) {
        // PER, PBR 계산 (20점 만점)
        return 15; // (임시 반환값)
    }

    // [가산점] 미래 투자 적극성 (10점 만점)
    private int calculateInvestment(FinancialStatement fs) {
        // 매출액 대비 (R&D + CapEx) 비율 계산
        BigDecimal revenue = fs.getRevenue();
        BigDecimal investSum = fs.getResearchAndDevelopment().add(fs.getCapitalExpenditure());

        // 예: 매출의 15% 이상을 투자하면 10점 만점
        // BigDecimal 연산 로직 필요
        return 10;
    }

    // [과락] 즉시 0점 처리 조건
    private boolean isDisqualified(FinancialStatement fs) {
        // 부채비율 400% 초과 OR 영업이익 적자 등
        return false;
    }

    // [페널티] 거시 경제 악화 (-10점)
    private int applyMacroPenalty(MacroEconomicData macro) {
        // 장단기 금리차 역전 (10년물 - 2년물 < 0)
        if (macro.getUs10yTreasuryYield() - macro.getUs2yTreasuryYield() < 0) {
            return 10; // 감점 10점
        }
        return 0;
    }

    // [페널티] 고금리 시기 위험 투자 (-15점)
    private int applyRiskyInvestmentPenalty(FinancialStatement fs, MacroEconomicData macro) {
        boolean isHighInterest = macro.getUs10yTreasuryYield() > 4.0; // 금리 4% 이상
        boolean isHighDebt = true; // (가정) 부채비율 높음 체크 로직
        boolean isAggressiveInvest = true; // (가정) 투자 적극성 높음 체크

        if (isHighInterest && isHighDebt && isAggressiveInvest) {
            return 15; // 감점 15점
        }
        return 0;
    }
}
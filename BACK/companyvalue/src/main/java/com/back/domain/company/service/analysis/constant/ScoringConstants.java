package com.back.domain.company.service.analysis.constant;

public class ScoringConstants {

    // --- 업종 ---
    public static final String SECTOR_FINANCIAL = "Financial Services";

    // -- 자본 잠식 기준 --
    // 일반 기업의 자본 잠식 부채비율 기준 (400%)
    public static final double DEBT_RATIO_LIMIT_GENERAL = 400.0;
    // 금융업은 고객들의 예금도 부채로 포함
    // 따라서 일반기업보다 자본 잠식 기준 완화(1500%)
    public static final double DEBT_RATIO_LIMIT_FINANCIAL = 1500.0;

    // --- 페널티(Penalty) 점수 및 기준 ---
    public static final int PENALTY_SCORE_MACRO = 10;               // 장단기 금리차 역전 시
    public static final int PENALTY_SCORE_HiGH_DEBT_IN_HIGH_RATE = 15; // 이자율이 높고 부채 비율도 높을 때
    public static final int PENALTY_SCORE_RISKY_INVESTMENT = 10;    // 위험 투자 시
    public static final int PENALTY_SCORE_CAPITAL_IMPAIRMENT = 40;  // 자본잠식 시 (매우 큰 페널티)
    public static final int PENALTY_SCORE_EXCESSIVE_DEBT = 20;      // 부채비율 400% 초과 시

    public static final double HIGH_INTEREST_RATE_THRESHOLD = 4.0;  // 고금리 기준 (4.0%)
    public static final double HIGH_DEBT_RATIO_GENERAL = 200.0;     // 고부채 기준 (일반)
    public static final double HIGH_DEBT_RATIO_FINANCIAL = 1000.0;  // 고부채 기준 (금융업)
    public static final double AGGRESSIVE_INVESTMENT_RATIO = 10.0;  // 공격적 투자 기준 (매출 대비 10%)

    // --- 저점 매수(Opportunity) 기준 ---
    public static final int OPPORTUNITY_VALUATION_THRESHOLD = 20;   // 밸류에이션 점수 기준

    // --- 등급(Grade) 기준 ---
    public static final int GRADE_S_THRESHOLD = 90;
    public static final int GRADE_A_THRESHOLD = 80;
    public static final int GRADE_B_THRESHOLD = 70;
    public static final int GRADE_C_THRESHOLD = 60;

    private ScoringConstants() {} // 인스턴스화 방지
}

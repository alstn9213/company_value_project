import { useMemo } from "react";
import { CompanyScoreResponse } from "../../../types/company";
import { MAX_SCORES } from "../../company/constants/maxScores";
import { ChartDataPoint } from "../types/chartDataPoint";


export const useScoreAnalytics = (score: CompanyScoreResponse | undefined) => {
  // useMemo를 사용하여 불필요한 재연산 방지
  const analytics = useMemo(() => {
    if (!score) {
      return null;
    }

    const baseScore = score.stabilityScore + score.profitabilityScore + score.valuationScore + score.investmentScore;
    const penaltyPoints = Math.max(0, baseScore - score.totalScore);

    const chartData: ChartDataPoint[] = [
      { subject: "안정성", score: score.stabilityScore, fullMark: MAX_SCORES.STABILITY },
      { subject: "수익성", score: score.profitabilityScore, fullMark: MAX_SCORES.PROFITABILITY },
      { subject: "내재가치", score: score.valuationScore, fullMark: MAX_SCORES.VALUATION },
      { subject: "미래투자", score: score.investmentScore, fullMark: MAX_SCORES.INVESTMENT },
    ];

    return { penaltyPoints, chartData };
  }, [score]);

  return analytics;
};
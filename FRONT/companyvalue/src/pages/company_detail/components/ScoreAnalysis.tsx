import { TrendingUp } from "lucide-react";
import ScoreRadarChart, { ChartDataPoint } from "./charts/ScoreRadarChart";
import RiskPenaltyAlert from "./analysis/RiskPenaltyAlert";
import TotalScoreDisplay from "./analysis/TotalScoreDisplay";
import ScoreDetailList from "./analysis/ScoreDetailList";

interface CompanyScore {
  ticker: string;
  name: string;
  grade: string;
  totalScore: number;
  stabilityScore: number;
  profitabilityScore: number;
  valuationScore: number;
  investmentScore: number;
  isOpportunity: boolean;
}

const MAX_SCORES = {
  STABILITY: 40,
  PROFITABILITY: 30,
  VALUATION: 20,
  INVESTMENT: 10,
};

interface Props {
  score: CompanyScore;
}

const ScoreAnalysis = ({ score }: Props) => {
  const baseScore = score.stabilityScore + score.profitabilityScore + score.valuationScore + score.investmentScore;
  const penaltyPoints = Math.max(0, baseScore - score.totalScore);

  const chartData: ChartDataPoint[] = [
    { subject: "안정성", score: score.stabilityScore, fullMark: MAX_SCORES.STABILITY },
    { subject: "수익성", score: score.profitabilityScore, fullMark: MAX_SCORES.PROFITABILITY },
    { subject: "내재가치", score: score.valuationScore, fullMark: MAX_SCORES.VALUATION },
    { subject: "미래투자", score: score.investmentScore, fullMark: MAX_SCORES.INVESTMENT },
  ];
  
  return (
    <div className="h-full flex flex-col gap-4">
      {/* 1. 페널티/위험 경고 메시지 */}
      <RiskPenaltyAlert penaltyPoints={penaltyPoints} />

      {/* 2. 메인 분석 카드 */}
      <div className="bg-card border border-slate-700/50 rounded-xl p-6 flex-1 flex flex-col shadow-lg backdrop-blur-sm">
        <h3 className="text-lg font-bold text-white mb-6 flex items-center gap-2">
          <TrendingUp size={20} className="text-emerald-400" />
          분석 리포트
        </h3>

        {/* 종합 점수 표시 */}
        <TotalScoreDisplay totalScore={score.totalScore} />

        {/* 레이더 차트 영역 */}
        <div className="flex-1 min-h-[200px] flex items-center justify-center -ml-4">
          <ScoreRadarChart data={chartData} />
        </div>

        {/* 세부 점수 리스트 */}
        <ScoreDetailList score={score} />
      </div>
    </div>
  );
};

export default ScoreAnalysis;

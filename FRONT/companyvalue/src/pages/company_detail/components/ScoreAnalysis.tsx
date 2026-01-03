import { AlertTriangle, HelpCircle, TrendingUp } from "lucide-react";
import { getScoreColor } from "../../../utils/formatters";
import { SCORE_TERMS, TermDefinition } from "../constants/financialTerms";
import ScoreRadarChart, { ChartDataPoint } from "./charts/ScoreRadarChart";
import ScoreRow from "./rows/ScoreRow";

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
  const hasPenalty = penaltyPoints > 0;

  const chartData: ChartDataPoint[] = [
    { subject: '안정성', score: score.stabilityScore, fullMark: MAX_SCORES.STABILITY },
    { subject: '수익성', score: score.profitabilityScore, fullMark: MAX_SCORES.PROFITABILITY },
    { subject: '내재가치', score: score.valuationScore, fullMark: MAX_SCORES.VALUATION },
    { subject: '미래투자', score: score.investmentScore, fullMark: MAX_SCORES.INVESTMENT },
  ];
  
  return (
    <div className="h-full flex flex-col gap-4">
      {/* 1. 페널티/위험 경고 메시지 */}
      {hasPenalty && (
        <div className="p-4 bg-orange-500/10 border border-orange-500/30 rounded-xl text-sm text-orange-200 animate-in fade-in slide-in-from-top-2 duration-300">
          <p className="font-bold flex items-center gap-2 mb-1">
            <AlertTriangle size={18} className="text-orange-400" />
            리스크 페널티 적용
          </p>
          <p className="opacity-80 pl-6 leading-relaxed">
            재무 건전성 이슈(자본잠식 또는 고부채 등)로 인해 
            <strong> -{penaltyPoints}점</strong>의 페널티가 적용되었습니다.
          </p>
        </div>
      )}

      {/* 2. 메인 분석 카드 */}
      <div className="bg-card border border-slate-700/50 rounded-xl p-6 flex-1 flex flex-col shadow-lg backdrop-blur-sm">
        <h3 className="text-lg font-bold text-white mb-6 flex items-center gap-2">
          <TrendingUp size={20} className="text-emerald-400" />
          분석 리포트
        </h3>

        {/* 종합 점수 표시 */}
        <div className="text-center mb-8 relative">
          <span className="text-slate-400 text-sm uppercase tracking-wider">
            Total Score
          </span>
          <div
            className={`text-6xl font-black mt-2 tracking-tight ${getScoreColor(
              score.totalScore
            )}`}
          >
            {score.totalScore}
            <span className="text-2xl text-slate-600 font-medium ml-1">
              /100
            </span>
          </div>
        </div>

        {/* 레이더 차트 영역 */}
        <div className="flex-1 min-h-[200px] flex items-center justify-center -ml-4">
          {/* 변환된 데이터를 넘겨줌 */}
          <ScoreRadarChart data={chartData} />
        </div>

        {/* 세부 점수 프로그레스 바 */}
        <div className="mt-8 space-y-4 pt-6 border-t border-slate-800">
          <ScoreRow
            label="안정성 (부채/유동성)"
            value={score.stabilityScore}
            max={MAX_SCORES.STABILITY}
            color="bg-blue-500"
            term={SCORE_TERMS.stability}
          />
          <ScoreRow
            label="수익성 (ROE/마진)"
            value={score.profitabilityScore}
            max={MAX_SCORES.PROFITABILITY}
            color="bg-emerald-500"
            term={SCORE_TERMS.profitability}
          />
          <ScoreRow
            label="내재가치 (PER/PBR)"
            value={score.valuationScore}
            max={MAX_SCORES.VALUATION}
            color="bg-purple-500"
            term={SCORE_TERMS.valuation}
          />
          <ScoreRow
            label="미래투자 (R&D)"
            value={score.investmentScore}
            max={MAX_SCORES.INVESTMENT}
            color="bg-orange-500"
            term={SCORE_TERMS.investment}
          />
        </div>
      </div>
    </div>
  );
};


export default ScoreAnalysis;

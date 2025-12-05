import { AlertTriangle, TrendingUp } from "lucide-react";
import ScoreRadarChart from "../../../components/charts/ScoreRadarChart";
import { getScoreColor } from "../../../utils/formatters";

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

interface Props {
  score: CompanyScore;
}

const ScoreAnalysis = ({score}: Props) => {
  const isRisky = score.grade.includes("F") || score.totalScore === 0;

  return(
    <div className="h-full flex flex-col gap-4">
      
      {/* 1. 투자 위험 경고 메시지 (조건부 렌더링) */}
      {isRisky && (
        <div className="p-4 bg-red-500/10 border border-red-500/30 rounded-xl text-sm text-red-200 animate-in fade-in slide-in-from-top-2 duration-300">
          <p className="font-bold flex items-center gap-2 mb-1">
            <AlertTriangle size={18} className="text-red-400" />
            투자 위험 경고
          </p>
          <p className="opacity-80 pl-6 leading-relaxed">
            이 기업은 <strong>자본 잠식</strong> 상태이거나{" "}
            <strong>부채 비율이 과도(400% 초과)</strong>하여 평가 대상에서 제외되었습니다.
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
          <span className="text-slate-400 text-sm uppercase tracking-wider">Total Score</span>
          <div className={`text-6xl font-black mt-2 tracking-tight ${getScoreColor(score.totalScore)}`}>
            {score.totalScore}
            <span className="text-2xl text-slate-600 font-medium ml-1">/100</span>
          </div>
        </div>

        {/* 레이더 차트 영역 */}
        <div className="flex-1 min-h-[200px] flex items-center justify-center -ml-4">
          <ScoreRadarChart score={score} />
        </div>

        {/* 세부 점수 프로그레스 바 */}
        <div className="mt-8 space-y-4 pt-6 border-t border-slate-800">
          <ScoreRow
            label="안정성 (부채/유동성)"
            value={score.stabilityScore}
            max={40}
            color="bg-blue-500"
          />
          <ScoreRow
            label="수익성 (ROE/마진)"
            value={score.profitabilityScore}
            max={30}
            color="bg-emerald-500"
          />
          <ScoreRow
            label="내재가치 (PER/PBR)"
            value={score.valuationScore}
            max={20}
            color="bg-purple-500"
          />
          <ScoreRow
            label="미래투자 (R&D)"
            value={score.investmentScore}
            max={10}
            color="bg-orange-500"
          />
        </div>
      </div>
    </div>
  );
};

// 내부 컴포넌트: 점수 행 (재사용성 및 가독성을 위해 분리)
const ScoreRow = ({
  label,
  value,
  max,
  color = "bg-blue-500",
}: {
  label: string;
  value: number;
  max: number;
  color?: string;
}) => (
  <div className="flex flex-col gap-1.5">
    <div className="flex justify-between items-center text-xs">
      <span className="text-slate-400 font-medium">{label}</span>
      <span className="text-slate-300 font-mono">
        <span className="text-white font-bold">{value}</span> / {max}
      </span>
    </div>
    
    {/* 프로그레스 바 배경 */}
    <div className="w-full h-2 bg-slate-800 rounded-full overflow-hidden">
      {/* 실제 게이지 */}
      <div
        className={`h-full rounded-full transition-all duration-1000 ease-out ${color}`}
        style={{ width: `${Math.min((value / max) * 100, 100)}%` }}
      />
    </div>
  </div>
);

export default ScoreAnalysis;
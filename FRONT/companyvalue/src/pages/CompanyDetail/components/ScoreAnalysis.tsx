import { AlertTriangle, HelpCircle, TrendingUp } from "lucide-react";
import ScoreRadarChart from "../../../components/charts/ScoreRadarChart";
import { getScoreColor } from "../../../utils/formatters";
import { SCORE_TERMS, TermDefinition } from "../constants/financialTerms";

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
            term={SCORE_TERMS.stability}
          />
          <ScoreRow
            label="수익성 (ROE/마진)"
            value={score.profitabilityScore}
            max={30}
            color="bg-emerald-500"
            term={SCORE_TERMS.profitability}
          />
          <ScoreRow
            label="내재가치 (PER/PBR)"
            value={score.valuationScore}
            max={20}
            color="bg-purple-500"
            term={SCORE_TERMS.valuation}
          />
          <ScoreRow
            label="미래투자 (R&D)"
            value={score.investmentScore}
            max={10}
            color="bg-orange-500"
            term={SCORE_TERMS.investment}
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
  term,
}: {
  label: string;
  value: number;
  max: number;
  color?: string;
  term?: TermDefinition;
}) => (
  // group 클래스를 추가하여 호버 상태를 자식 요소가 감지할 수 있게 함
  <div className="flex flex-col gap-1.5 group relative">
    <div className="flex justify-between items-center text-xs">
      {/* 라벨에 마우스를 올리면 툴팁이 뜨도록 설정 */}
      <div className="flex items-center gap-1.5 cursor-help">
        <span className="text-slate-400 font-medium border-b border-dotted border-slate-600 group-hover:text-white group-hover:border-slate-400 transition-colors">
          {label}
        </span>
        {/* 작은 물음표 아이콘 (선택 사항) */}
        <HelpCircle size={12} className="text-slate-600 group-hover:text-slate-400 transition-colors" />
      </div>
      
      <span className="text-slate-300 font-mono">
        <span className="text-white font-bold">{value}</span> / {max}
      </span>
    </div>

    {/* 프로그레스 바 배경 */}
    <div className="w-full h-2 bg-slate-800 rounded-full overflow-hidden">
      <div
        className={`h-full rounded-full transition-all duration-1000 ease-out ${color}`}
        style={{ width: `${Math.min((value / max) * 100, 100)}%` }}
      />
    </div>

    {/* 툴팁 (Hover 시 등장) */}
    {term && (
      <div className="absolute bottom-full left-0 mb-2 w-72 p-4 bg-slate-900/95 border border-slate-700 rounded-lg shadow-xl backdrop-blur-md z-50 hidden group-hover:block animate-in fade-in zoom-in-95 duration-200">
        <h4 className="font-bold text-slate-100 mb-2 text-sm">{term.title}</h4>
        <p className="text-xs text-slate-300 leading-relaxed whitespace-pre-line">
          {term.description}
        </p>
        {/* 말풍선 꼬리 */}
        <div className="absolute top-full left-6 -mt-1.5 border-4 border-transparent border-t-slate-700" />
      </div>
    )}
  </div>
);

export default ScoreAnalysis;
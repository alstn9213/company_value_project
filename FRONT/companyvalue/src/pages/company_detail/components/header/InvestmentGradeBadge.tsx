import { Sparkles } from "lucide-react";
import { getGradeColor } from "../../../../utils/formatters";

interface Props {
  grade: string;
  isOpportunity: boolean;
}

export const InvestmentGradeBadge = ({ grade, isOpportunity }: Props) => {
  return (
    <div className="flex flex-col items-center">
      <span className="text-slate-400 text-sm mb-1">투자 적합 등급</span>
      <div className={`w-20 h-20 rounded-full border-4 flex items-center justify-center text-4xl font-bold shadow-[0_0_20px_rgba(0,0,0,0.3)] ${getGradeColor(grade)}`}>
        {grade}
      </div>

      {/* pbr, per이 낮으면 띄우는 알람 */}
      {isOpportunity && (
        <div className="mt-3 flex items-center gap-1 px-3 py-1 rounded-full bg-blue-500/20 border border-blue-400 text-blue-300 text-xs font-bold animate-pulse shadow-[0_0_10px_rgba(59,130,246,0.5)]">
          <Sparkles size={12} className="text-blue-300 fill-blue-300" />
          <span>저점 매수 기회</span>
        </div>
      )}
    </div>
  );
};
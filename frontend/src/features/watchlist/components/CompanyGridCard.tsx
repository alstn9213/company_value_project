import { ReactNode } from "react";
import { Link } from "react-router-dom";
import { TrendingUp } from "lucide-react";
import { WatchlistResponse } from "../../../types/watchlist";
import { getGradeColor, getScoreColor } from "../../../utils/formatters";

interface CompanyGridCardProps {
  item: WatchlistResponse;
  action?: ReactNode; // 버튼을 외부에서 주입받음 (Optional)
}

export const CompanyGridCard = ({ item, action }: CompanyGridCardProps) => {
  const { company } = item;

  return (
    <Link
      to={`/company/${company.ticker}`}
      className="group relative bg-card border border-slate-700/50 rounded-xl p-6 hover:border-blue-500/50 transition-all duration-300 hover:-translate-y-1 hover:shadow-lg block"
    >
      <div className="flex justify-between items-start mb-4">
        <div className="min-w-0">
          <span className="inline-block px-2 py-0.5 rounded text-xs font-bold bg-slate-800 text-slate-400 mb-2">
            {company.ticker}
          </span>
          <h3 className="text-xl font-bold text-white truncate pr-4">
            {company.name}
          </h3>
        </div>

        {/* 등급 뱃지 */}
        <div
          className={`w-10 h-10 flex-shrink-0 rounded-full border-2 flex items-center justify-center text-lg font-bold ${getGradeColor(
            company.grade
          )}`}
        >
          {company.grade}
        </div>
      </div>

      <div className="flex items-center justify-between mt-4 pt-4 border-t border-slate-700/50">    
        <div className="flex items-center gap-2">
          <TrendingUp size={16} className="text-slate-500" />
          <span className={`font-bold ${getScoreColor(company.totalScore)}`}>
            {company.totalScore}점
          </span>
        </div>

        {/* 삭제 버튼 */}
        <div className="z-20 relative">
            {action}
        </div>
        
      </div>

    </Link>
  );
};

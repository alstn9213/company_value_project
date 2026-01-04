import { Trophy } from "lucide-react";
import { ScoreResult } from "../../../types/company";
import { Link } from "react-router-dom";
import { getGradeColor, getScoreColor } from "../../../utils/formatters";

interface TopRatedCompaniesProps {
  companies: ScoreResult[];
}

const TopRatedCompanies = ({ companies }: TopRatedCompaniesProps) => {
  if(!companies || companies.length === 0) return null;

  return (
    <section className="space-y-4">
      <h2 className="text-2xl font-bold text-white flex items-center gap-2 px-1">
        <Trophy className="text-yellow-400 fill-yellow-400" />
        <span className="bg-clip-text text-transparent bg-gradient-to-r from-yellow-200 to-amber-400">
          "이달의 추천 우량주 Top 5"
        </span>
      </h2>

      <div className="flex gap-5 overflow-x-auto pb-6 pt-2 px-1 scrollbar-thin scrollbar-thumb-slate-700 scrollbar-track-transparent">
        {companies.slice(0, 5).map((item, idx) => (
          <Link
            key={item.ticker}
            to={`/company/${item.ticker}`}
            className="min-w-[260px] bg-gradient-to-b from-slate-800 to-slate-900 border border-slate-700 rounded-2xl p-5 shadow-lg hover:-translate-y-2 transition-transform duration-300 relative group overflow-hidden"
          >
            {/* 1등 강조 효과 */}
            {idx === 0 && (
              <div className="absolute top-0 right-0 bg-yellow-500 text-black text-xs font-bold px-3 py-1 rounded-bl-xl shadow-md z-10">
                "1st Pick"
              </div>
            )}

            <div className="flex justify-between items-start mb-4">
              <div
                className={`w-12 h-12 rounded-xl flex items-center justify-center text-xl font-bold border-2 ${getGradeColor(
                  item.grade
                )}`}
              >
                {item.grade}
              </div>
              <div className="text-right">
                <span
                  className={`text-2xl font-bold ${getScoreColor(
                    item.totalScore
                  )}`}
                >
                  {item.totalScore}
                </span>
                <span className="text-xs text-slate-500 block">"점"</span>
              </div>
            </div>

            <div className="space-y-1">
              <h3 className="text-lg font-bold text-white truncate group-hover:text-blue-400 transition-colors">
                {item.ticker}
              </h3>
              <p className="text-sm text-slate-400 truncate">{item.name}</p>
            </div>

            {/* 하단 점수 요약 바 */}
            <div className="mt-4 pt-4 border-t border-slate-700/50 flex justify-between text-xs text-slate-500">
              <div className="flex flex-col gap-1">
                <span>"안정성"</span>
                <span className="text-slate-300">
                  {item.stabilityScore}/40
                </span>
              </div>
              <div className="flex flex-col gap-1 text-right">
                <span>"수익성"</span>
                <span className="text-slate-300">
                  {item.profitabilityScore}/30
                </span>
              </div>
            </div>
          </Link>
        ))}
      </div>
    </section>
  );
};

export default TopRatedCompanies;


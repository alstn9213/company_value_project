import { Link } from "react-router-dom";

export const TopRankingHeader = () => {
  return (
    <div className="flex items-center justify-between border-b border-slate-700 pb-2">
      <h2 className="text-lg font-bold text-slate-100">Top Ranking</h2>
      <Link
        to="/companies"
        className="text-xs text-blue-400 hover:underline"
      >
        전체보기
      </Link>
    </div>
  );
};
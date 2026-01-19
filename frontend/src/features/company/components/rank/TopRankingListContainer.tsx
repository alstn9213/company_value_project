import { RankingTableBody } from "./RankingTableBody";
import { useTopRankingCompanies } from "../../hooks/useTopRankingCompanies";
import { Link } from "react-router-dom";


export const TopRankingListContainer = () => {
  const { rankings, isLoading, isError, refetch } = useTopRankingCompanies();

  return (
    <section className="space-y-4 xl:col-span-3">
      {/* 헤더 */}
      <div className="flex items-center justify-between border-b border-slate-700 pb-2">
        <h2 className="text-lg font-bold text-slate-100">
          Top Ranking
        </h2>
        <Link
          to="/companies"
          className="text-xs text-blue-400 hover:underline"
        >
          전체보기
        </Link>
      </div>
      {/* 테이블 영역 */}
      <div className="overflow-hidden rounded-lg border border-slate-700 bg-slate-800">
        <table className="w-full text-left text-sm">
          <thead className="bg-slate-900 text-slate-400">
            <tr>
              <th className="py-3 pl-4 font-medium w-12">순위</th>
              <th className="py-3 font-medium">회사</th>
              <th className="py-3 text-center font-medium w-16">점수</th>
              <th className="py-3 text-center font-medium w-16">등급</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-700/50">
            <RankingTableBody 
              companies={rankings} 
              isLoading={isLoading}
              isError={isError}
              onRetry={refetch} 
            />
          </tbody>
        </table>
      </div>
    </section>
  );
};

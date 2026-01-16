import { RankingTableHeader } from "../ui/p_ranking/RankingTableHeader";
import { TopRankingHeader } from "../ui/p_ranking/RankingHeader";
import { RankingTableBody } from "../ui/p_ranking/RankingTableBody";
import { useTopRankingCompanies } from "../hooks/useCompanyRanking";


export const TopRankingListSection = () => {
  const { data: companies, isLoading } = useTopRankingCompanies();

  return (
    <section className="space-y-4 xl:col-span-3">
      {/* 헤더 */}
      <TopRankingHeader />
      {/* 테이블 영역 */}
      <div className="overflow-hidden rounded-lg border border-slate-700 bg-slate-800">
        <table className="w-full text-left text-sm">
          <RankingTableHeader />
          <tbody className="divide-y divide-slate-700/50">
            <RankingTableBody 
              companies={companies || []} 
              isLoading={isLoading} 
            />
          </tbody>
        </table>
      </div>
      </section>
  );
};

import { RankingTableHeader } from "../ui/p_ranking/RankingTableHeader";
import { TopRankingHeader } from "../ui/p_ranking/RankingHeader";
import { RankingTableBody } from "../ui/p_ranking/RankingTableBody";
import { useTopRankingCompanies } from "../hooks/useCompanyRanking";
import { ErrorState } from "../../../components/ui/ErrorState";


export const TopRankingListSection = () => {
  const { 
    rankings,
    isLoading, 
    isError, 
    refetch 
  } = useTopRankingCompanies();

  if (isError) {
    return (
      <section className="space-y-4 xl:col-span-3">
        <TopRankingHeader />
        <div className="rounded-lg border border-slate-700 bg-slate-800 p-6">
           <ErrorState 
             title="랭킹 정보를 불러올 수 없습니다" 
             onRetry={refetch} 
           />
        </div>
      </section>
    );
  }

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
              companies={rankings} 
              isLoading={isLoading} 
            />
          </tbody>
        </table>
      </div>
      </section>
  );
};

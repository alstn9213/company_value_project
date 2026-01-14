import { Link } from "react-router-dom";
import { CompanyScoreResponse } from "../../../types/company";
import { RankingItemRow } from "../ui/p_ranking/RankingItemRow";
import { RankingSkeleton } from "../ui/skeletons/RankingSkeleton";
import { RankingTableHeader } from "../ui/p_ranking/RankingTableHeader";
import { TopRankingEmptyState } from "../ui/states/TopRankingEmptyState";


interface TopRankingListSectionProps {
  companies: CompanyScoreResponse[];
  isLoading: boolean;
}

export const TopRankingListSection = ({ companies, isLoading }: TopRankingListSectionProps) => {
  const renderTableBody = () => {
    if (isLoading) {
      return <RankingSkeleton />;
    }

    if (companies.length === 0) {
      return <TopRankingEmptyState />;
    }

    return companies.slice(0, 10).map((item, index) => (
      <RankingItemRow key={item.ticker} data={item} rank={index + 1} />
    ));

  };

    return (
    <section className="space-y-4 xl:col-span-3">
      {/* 섹션 헤더 영역 */}
      <div className="flex items-center justify-between border-b border-slate-700 pb-2">
        <h2 className="text-lg font-bold text-slate-100">Top Ranking</h2>
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
          <RankingTableHeader />
          <tbody className="divide-y divide-slate-700/50">
            {renderTableBody()}
          </tbody>
        </table>
      </div>
    </section>
  );
};

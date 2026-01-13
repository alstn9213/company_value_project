import { Link } from "react-router-dom";
import { ScoreResult } from "../../../types/company";
import { RankingEmptyRow } from "./rows/RankingEmptyRow";
import { RankingItemRow } from "./rows/RankingItemRow";
import { RankingSkeletonRows } from "./rows/RankingSkeletonRows";
import { RankingTableHeader } from "../layouts/RankingTableHeader";


interface TopRankingListSectionProps {
  companies: ScoreResult[];
  isLoading: boolean;
}

export const TopRankingListSection = ({ companies, isLoading }: TopRankingListSectionProps) => {
  const renderTableBody = () => {
    if (isLoading) {
      return <RankingSkeletonRows />;
    }

    if (companies.length === 0) {
      return <RankingEmptyRow />;
    }

    return companies
      .slice(0, 10)
      .map((item, index) => (
        <RankingItemRow key={item.ticker} item={item} rank={index + 1} />
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

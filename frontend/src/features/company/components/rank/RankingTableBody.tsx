import { CompanyScoreResponse } from "../../../../types/company";
import { RankingSkeleton } from "../../ui/skeletons/RankingSkeleton";
import { TopRankingEmptyState } from "../../ui/states/TopRankingEmptyState";
import { RankingItemRow } from "../../ui/p_ranking/RankingItemRow";

interface RankingTableBodyProps {
  companies: CompanyScoreResponse[];
  isLoading: boolean;
}

export const RankingTableBody = ({ companies, isLoading }: RankingTableBodyProps) => {
  if (isLoading) {
    return <RankingSkeleton />;
  }

  if (!companies) {
    return null;
  }

  if (companies.length === 0) {
    return <TopRankingEmptyState />;
  }

  return (
    <>
      {companies.slice(0, 10).map((item, index) => (
        <RankingItemRow key={item.ticker} data={item} rank={index + 1} />
      ))}
    </>
  );
};
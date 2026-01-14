import { CompanyScoreResponse } from "../../../../types/company";
import { RankingSkeleton } from "../skeletons/RankingSkeleton";
import { TopRankingEmptyState } from "../states/TopRankingEmptyState";
import { RankingItemRow } from "./RankingItemRow";

interface RankingTableBodyProps {
  companies: CompanyScoreResponse[];
  isLoading: boolean;
}

export const RankingTableBody = ({ companies, isLoading }: RankingTableBodyProps) => {
  if (isLoading) {
    return <RankingSkeleton />;
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
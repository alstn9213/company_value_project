import { CompanyScoreResponse } from "../../../../types/company";
import { RankingSkeleton } from "../../ui/skeletons/RankingSkeleton";
import { TopRankingEmptyState } from "../../ui/states/TopRankingEmptyState";
import { RankingItemRow } from "../../ui/p_ranking/RankingItemRow";
import { ErrorState } from "../../../../components/ui/ErrorState";

interface RankingTableBodyProps {
  companies: CompanyScoreResponse[];
  isLoading: boolean;
  isError: boolean;
  onRetry?: () => void;
}

export const RankingTableBody = ({ 
  companies, 
  isLoading,
  isError,
  onRetry 
}: RankingTableBodyProps) => {
  if (isLoading) {
    return <RankingSkeleton />;
  }

  if (isError) {
    return (
      <tr>
        <td colSpan={4} className="py-10">
          <ErrorState 
            title="랭킹을 불러올 수 없습니다"
            onRetry={onRetry}
          />
        </td>
      </tr>
    );
  }

  if (!companies || companies.length === 0) {
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
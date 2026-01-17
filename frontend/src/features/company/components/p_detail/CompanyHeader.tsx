import { CompanySummaryResponse, CompanyScoreResponse } from "../../../../types/company";
import { WatchlistButton } from "../../../watchlist/ui/WatchlistButton";
import { useAddWatchlist } from "../../../watchlist/hooks/useAddWatchlist";
import { CompanyProfile } from "../../ui/p_detail/CompanyProfile";
import { InvestmentGradeBadge } from "../../ui/p_detail/InvestmentGradeBadge";
import { Skeleton } from "../../../../components/ui/Skeleton";

interface CompanyHeaderProps {
  info: CompanySummaryResponse | undefined;
  score: CompanyScoreResponse | undefined;
  isLoading: boolean;
}

export const CompanyHeader = ({ info, score, isLoading }: CompanyHeaderProps) => {
  const { addWatchlist, isPending } = useAddWatchlist();

   if (isLoading) {
    return <Skeleton/>;
  }

  if (!info) {
    return null;
  }

  if (!score) {
    return null;
  }


 
 return (
    <div className="bg-card border border-slate-700/50 rounded-2xl p-8 shadow-lg backdrop-blur-sm flex flex-col md:flex-row justify-between items-center gap-6">
      {/* 좌측: 기업 프로필 */}
      <CompanyProfile
        ticker={info.ticker}
        name={info.name}
        exchange={info.exchange}
        sector={info.sector}
      />

      {/* 우측: 관심 목록 등록 버튼 및 등급 */}
      <div className="flex items-center gap-6">
        <WatchlistButton
          onClick={() => addWatchlist(info.ticker)}
          isPending={isPending}
        />
        <InvestmentGradeBadge
          grade={score.grade}
          isOpportunity={score.isOpportunity}
        />
      </div>
    </div>
  );
};

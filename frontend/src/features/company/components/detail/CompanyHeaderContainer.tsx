import { Building2 } from "lucide-react";
import { EmptyState } from "../../../../components/ui/EmptyState";
import { CompanySummaryResponse, CompanyScoreResponse } from "../../../../types/company";
import { WatchlistButton } from "../../ui/p_detail/WatchlistButton";
import { useAddWatchlist } from "../../hooks/useAddWatchlist";
import { CompanyProfile } from "../../ui/p_detail/CompanyProfile";
import { InvestmentGradeBadge } from "../../ui/p_detail/InvestmentGradeBadge";
import { CompanyHeaderSkeleton } from "../../ui/skeletons/CompanyHeaderSkeleton";
import { ErrorState } from "../../../../components/ui/ErrorState";

interface CompanyHeaderProps {
  info: CompanySummaryResponse | undefined;
  score: CompanyScoreResponse | undefined;
  isLoading: boolean;
  isError: boolean;
  onRetry?: () => void;
}

export const CompanyHeaderContainer = ({ 
  info, 
  score, 
  isLoading, 
  isError, 
  onRetry 
}: CompanyHeaderProps) => {
  const { addWatchlist, isPending } = useAddWatchlist();

   if (isLoading) {
    return <CompanyHeaderSkeleton />;
  }

  if (isError) {
    return (
      <div className="bg-card border border-slate-700/50 rounded-2xl p-8 shadow-lg backdrop-blur-sm flex items-center justify-center min-h-[200px]">
        <ErrorState
          title="기업 정보를 불러올 수 없습니다"
          onRetry={onRetry}
        />
      </div>
    );
  }

  if (!info || !score) {
    return (
      <div className="bg-card border border-slate-700/50 rounded-2xl p-8 shadow-lg backdrop-blur-sm flex items-center justify-center min-h-[200px]">
        <EmptyState
          icon={<Building2 className="w-12 h-12 text-slate-600 mb-4" />}
          title="기업 정보를 불러올 수 없습니다"
          description="해당 기업의 기본 정보나 평가 점수가 존재하지 않습니다."
        />
      </div>
    );
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

import { EmptyState } from "../../../components/ui/EmptyState";
import { ErrorState } from "../../../components/ui/ErrorState";
import { LoadingState } from "../../../components/ui/LoadingState";
import { WatchlistResponse } from "../../../types/watchlist";
import { CompanyGridCard } from "./CompanyGridCard";
import { WatchlistDeleteButton } from "./WatchlistDeleteButton";

interface WatchlistContentProps {
  watchlist: WatchlistResponse[] | undefined;
  isLoading: boolean;
  isError: boolean;
  refetch: () => void;
  handleDelete: (id: number) => void;
}

const WatchlistStateWrapper = ({ children }: { children: React.ReactNode }) => (
  <div className="py-20 px-4 flex justify-center">
    {children}
  </div>
);

export const WatchlistContent = ({ 
  watchlist, 
  isLoading, 
  isError, 
  refetch, 
  handleDelete 
}: WatchlistContentProps) => {
  
  if (isLoading) {
    return (
      <WatchlistStateWrapper>
        <LoadingState message="관심 종목을 불러오는 중..." />
      </WatchlistStateWrapper>
    );
  }

  if (isError) {
    return (
      <WatchlistStateWrapper>
        <ErrorState 
          title="관심 종목을 불러오지 못했습니다"
          onRetry={refetch}
        />
      </WatchlistStateWrapper>
    );
  }

  if (!watchlist || watchlist.length === 0) {
    return (
      <WatchlistStateWrapper>
        <EmptyState
          title="관심 종목이 비어있습니다."
          description="기업 목록에서 마음에 드는 기업을 추가해보세요."
        />
      </WatchlistStateWrapper>
    );
  }

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
      {watchlist.map((item) => (
        <CompanyGridCard
          key={item.watchlistId}
          item={item}
          action={<WatchlistDeleteButton onClick={() => handleDelete(item.watchlistId)}/>}
        />
      ))}
    </div>
  );
};
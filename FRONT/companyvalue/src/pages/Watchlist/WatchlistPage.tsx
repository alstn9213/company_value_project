import { useWatchlist } from "../../features/watchlist/hooks/useWatchlist"; 
import LoadingState from "../../components/common/LoadingState";
import EmptyState from "../../components/common/EmptyState";
import WatchlistCard from "../../features/watchlist/components/WatchlistCard";

const WatchlistPage = () => {
  const { watchlist, isLoading, handleDelete } = useWatchlist();

  if (isLoading) {
    return <LoadingState message="관심 종목을 불러오는 중..." />;
  }

  if (!watchlist || watchlist.length === 0) {
    return (
      <div className="max-w-7xl mx-auto py-20 px-4">
        <EmptyState
          title="관심 종목이 비어있습니다."
          description="기업 목록에서 마음에 드는 기업을 추가해보세요."
        />
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-yellow-400 to-orange-400">
          My Watchlist
        </h1>
        <p className="text-slate-400 mt-2">
          내가 찜한 기업들의 최신 평가 등급을 확인하세요.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        {watchlist.map((item) => (
          <WatchlistCard
            key={item.watchlistId}
            item={item}
            onDelete={handleDelete}
          />
        ))}
      </div>
    </div>
  );
};

export default WatchlistPage;
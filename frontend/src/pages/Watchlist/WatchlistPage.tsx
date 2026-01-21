import { useWatchlist, WatchlistContent } from "../../features/watchlist";
import { Pagination } from "../../components/ui/Pagination";
import { usePagination } from "../../hooks/usePagination";

const ITEMS_PER_PAGE = 8; // 한 페이지당 보여줄 아이템 개수

export const WatchlistPage = () => {
  const { watchlist, isLoading, handleDelete, isError, refetch } = useWatchlist();
  
  const { 
    currentPage, 
    totalPages, 
    paginatedData, 
    setCurrentPage, 
    totalItems 
  } = usePagination({
    data: watchlist,
    itemsPerPage: ITEMS_PER_PAGE
  });

  return (
    <div className="max-w-7xl mx-auto space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-yellow-400 to-orange-400">
          관심 종목
        </h1>
        <p className="text-slate-400 mt-2">
          내가 찜한 기업들의 최신 평가 등급을 확인하세요.
        </p>
      </div>

      <WatchlistContent 
        watchlist={paginatedData}
        isLoading={isLoading}
        isError={isError}
        refetch={refetch}
        handleDelete={handleDelete}
      />

      {!isLoading && !isError && totalItems > 0 && (
        <div className="mt-8">
          <Pagination 
            currentPage={currentPage}
            totalPages={totalPages}
            onPageChange={setCurrentPage}
          />
        </div>
      )}
    </div>
  );
};

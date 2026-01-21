import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { AxiosError } from "axios";
import { watchlistApi } from "../api/watchlistApi";
import { watchlistKeys } from "../api/queryKeys";
import { WatchlistResponse } from "../../../types/watchlist";
import { ApiErrorData } from "../../../types/api";

export const useWatchlist = () => {
  const queryClient = useQueryClient();

  const { data: watchlist, isLoading, isError, refetch } = useQuery<WatchlistResponse[], AxiosError<ApiErrorData>>({
    queryKey: watchlistKeys.lists(),
    queryFn: watchlistApi.getMyWatchlist,
  });

  const deleteMutation = useMutation({
    mutationFn: watchlistApi.remove,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: watchlistKeys.lists() });
    },
    onError: (error) => {
      console.error("삭제 실패:", error);
      alert("삭제 중 오류가 발생했습니다.");
    },
  });

  const handleDelete = (id: number) => {
    if (confirm("관심 목록에서 삭제하시겠습니까?")) {
      deleteMutation.mutate(id);
    }
  };

  return {
    watchlist,
    isLoading,
    isError,
    refetch,
    handleDelete,
    isDeleting: deleteMutation.isPending,
  };
};
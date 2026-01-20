import { useMutation, useQueryClient } from "@tanstack/react-query";
import { AxiosError } from "axios";
import { useRequireAuth } from "../../../hooks/useRequireAuth";
import { watchlistApi } from "../../watchlist/api/watchlistApi";
import { watchlistKeys } from "../../watchlist/api/queryKeys";

export const useAddWatchlist = () => {
  const queryClient = useQueryClient();
  const { withAuth } = useRequireAuth();

  const mutation = useMutation({
    mutationFn: (ticker: string) => watchlistApi.add(ticker),
    onSuccess: () => {
      alert("관심 종목에 추가되었습니다.");
      queryClient.invalidateQueries({ queryKey: watchlistKeys.lists() });
    },
    onError: (error: AxiosError) => {
      if (error.response?.status === 400) {
        alert("이미 관심 목록에 존재하는 기업입니다.");
      } else {
        alert("추가 중 오류가 발생했습니다.");
      }
    },
  });

  const addWatchlist = (ticker: string) => {
    // 로그인이 돼 있을 때만 실행
    withAuth(() => {
      mutation.mutate(ticker);
    });
  };

  return {
    addWatchlist,
    isPending: mutation.isPending,
  };
};
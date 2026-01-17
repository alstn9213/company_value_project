import { useQuery } from "@tanstack/react-query";
import { companyApi } from "../api/companyApi";
import { CompanyScoreResponse } from "../../../types/company";
import { getErrorMessage } from "../../../utils/errorHandler";
import { AxiosError } from "axios";
import { ApiErrorData } from "../../../types/auth";

const RANKING_KEYS = {
  all: ["ranking"] as const,
  top: () => [...RANKING_KEYS.all, "top"] as const,
};

export interface TopRankingHookResult {
  rankings: CompanyScoreResponse[];
  isLoading: boolean;
  isError: boolean;
  errorMessage: string | undefined;
  refetch: () => void;
}

export const useTopRankingCompanies = (): TopRankingHookResult => {
  const { 
    data, 
    isLoading, 
    isError, 
    error, 
    refetch 
  } = useQuery<CompanyScoreResponse[], AxiosError<ApiErrorData>>({
    queryKey: RANKING_KEYS.top(),
    queryFn: companyApi.getTopRanked,
    staleTime: 1000 * 60 * 15, // 15분간 캐시 유지 (랭킹은 자주 안 변함)
    gcTime: 1000 * 60 * 30,    // 30분간 메모리에 보관
  });

  return {
    // 데이터가 없으면 빈 배열 반환하여 컴포넌트에서 ?. 체크 제거
    rankings: data || [], 
    isLoading,
    isError,
    errorMessage: getErrorMessage(error),
    refetch,
  };
};
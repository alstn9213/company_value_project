import { useQuery } from "@tanstack/react-query";
import { companyApi } from "../api/companyApi";
import { CompanyScoreResponse } from "../../../types/company";
import { getErrorMessage } from "../../../utils/errorHandler";
import { AxiosError } from "axios";
import { ApiErrorData } from "../../../types/auth";
import { companyKeys } from "../api/queryKeys";

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
    queryKey: companyKeys.topRanking(),
    queryFn: companyApi.getTopRanked,    
    staleTime: 1000 * 60 * 15, // 15분 (자주 변하지 않는 데이터)
    gcTime: 1000 * 60 * 30,    // 30분
  });
  
  return {
    rankings: data || [], 
    isLoading,
    isError,
    errorMessage: getErrorMessage(error),
    refetch,
  };
};
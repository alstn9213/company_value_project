import { useQuery } from "@tanstack/react-query";
import { CompanyDetailResponse, CompanyScoreResponse, CompanySummaryResponse, FinancialStatementResponse } from "../../../types/company";
import { companyApi } from "../api/companyApi";
import { AxiosError } from "axios";
import { companyKeys } from "../api/queryKeys";

export interface CompanyDetailHookResult {
  summary: CompanySummaryResponse | undefined;
  score: CompanyScoreResponse | undefined;
  financial: FinancialStatementResponse | undefined;
  isLoading: boolean;
  isError: boolean;
  error: AxiosError | null;
  refetch: () => void;
}

export const useCompanyDetail = (ticker: string | undefined): CompanyDetailHookResult => {
  const { data, isLoading, isError, error, refetch } = useQuery<CompanyDetailResponse, AxiosError>({
    queryKey: ticker ? companyKeys.detail(ticker) : [],
    queryFn: () => companyApi.getDetail(ticker!),
    enabled: !!ticker, // ticker가 존재할 때만 실행
    staleTime: 1000 * 60 * 5, // 5분간 데이터를 신선한 상태로 유지
    retry: 1, // 실패 시 1회 재시도
  });

  return {
    // data가 없을 때(로딩 중 or 에러)를 대비해 안전하게 언패킹
    summary: data?.companySummary,
    score: data?.score,
    financial: data?.latestFinancial,
    isLoading,
    isError,
    error,
    refetch,
  };
};
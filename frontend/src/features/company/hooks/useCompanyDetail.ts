import { useQuery } from "@tanstack/react-query";
import { CompanyDetailResponse } from "../../../types/company";
import { companyApi } from "../api/companyApi";

const COMPANY_DETAIL_KEYS = {
  all: ["company"] as const,
  detail: (ticker: string) => [...COMPANY_DETAIL_KEYS.all, "detail", ticker] as const,
};

export const useCompanyDetail = (ticker: string | undefined) => {
  return useQuery<CompanyDetailResponse>({
    queryKey: ticker ? COMPANY_DETAIL_KEYS.detail(ticker) : [],
    queryFn: () => companyApi.getDetail(ticker!),
    enabled: !!ticker, // ticker가 존재할 때만 실행
    staleTime: 1000 * 60 * 5, // 5분간 데이터를 신선한 상태로 유지
    retry: 1, // 실패 시 1회 재시도
  });
};
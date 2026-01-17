import { useState } from "react";
import { AxiosError } from "axios";
import { companyApi } from "../api/companyApi";
import { CompanySummaryResponse } from "../../../types/company";
import { ApiErrorData } from "../../../types/auth";
import { useDebounce } from "../../../hooks/useDebounce";
import { keepPreviousData, useQuery } from "@tanstack/react-query";
import { getErrorMessage } from "../../../utils/errorHandler";
import { companyKeys } from "../api/queryKeys";

export interface CompanySearchHookResult {
  keyword: string;
  setKeyword: (keyword: string) => void;
  suggestions: CompanySummaryResponse[];
  isLoading: boolean;
  error: string | undefined;
  clearSearch: () => void;
}

export const useCompanySearch = (initialKeyword = ""): CompanySearchHookResult => {
  const [keyword, setKeyword] = useState(initialKeyword);
  const debouncedKeyword = useDebounce(keyword, 300);

  const { data, isLoading, error } = useQuery<CompanySummaryResponse[], AxiosError<ApiErrorData>>({
    queryKey: companyKeys.search(debouncedKeyword),
    queryFn: () => companyApi.search(debouncedKeyword),    
    enabled: !!debouncedKeyword && debouncedKeyword.trim().length > 0,
    staleTime: 1000 * 60 * 1, // 1분간 캐시
    placeholderData: keepPreviousData, // (선택사항) 타이핑 중 이전 검색결과 유지하여 깜빡임 방지
  });

  const clearSearch = () => {
    setKeyword("");
  };

  return {
    keyword,
    setKeyword,
    suggestions: data || [], 
    isLoading,
    error: getErrorMessage(error),
    clearSearch,
  };
};
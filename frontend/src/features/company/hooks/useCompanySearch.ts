import { useState } from "react";
import { AxiosError } from "axios";
import { companyApi } from "../api/companyApi";
import { CompanySummaryResponse } from "../../../types/company";
import { ApiErrorData } from "../../../types/auth";
import { useDebounce } from "../../../hooks/useDebounce";
import { useQuery } from "@tanstack/react-query";
import { getErrorMessage } from "../../../utils/errorHandler";

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
    queryKey: ["search", debouncedKeyword],
    queryFn: () => companyApi.search(debouncedKeyword),
    enabled: !!debouncedKeyword && debouncedKeyword.trim().length > 0, // 빈 문자열일 때 요청 방지
    staleTime: 1000 * 60 * 1, // 1분간 캐시 (같은 검색어 반복 요청 방지)
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
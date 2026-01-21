import { keepPreviousData, useQuery } from "@tanstack/react-query";
import { companyApi } from "../api/companyApi";
import { useState } from "react";
import { AxiosError } from "axios";
import { PageResponse, CompanySummaryResponse } from "../../../types/company";
import { getErrorMessage } from "../../../utils/errorHandler";
import { companyKeys } from "../api/queryKeys";
import { ApiErrorData } from "../../../types/api";

interface CompanyListHookResult {
  companies: CompanySummaryResponse[];
  currentPage: number;
  totalPages: number;
  isLastPage: boolean;
  showPagination: boolean;
  isLoading: boolean;
  isPlaceholderData: boolean;
  isError: boolean;
  errorMessage: string | undefined;
  sortOption: string;
  setPage: (page: number) => void;
  setSortOption: (sort: string) => void;
}

export const useCompanyList = (pageSize: number = 12): CompanyListHookResult => {
  const [page, setPage] = useState(0); // API 요청용 (0-based index)
  const [sortOption, setSortOption] = useState("score");

  const {
    data: pageData,
    isLoading: isPageLoading,
    isPlaceholderData,
    isError,
    error,
  } = useQuery<PageResponse<CompanySummaryResponse>, AxiosError<ApiErrorData>>({
    queryKey: companyKeys.list(page, pageSize, sortOption),
    queryFn: () => companyApi.getAll(page, pageSize, sortOption),
    placeholderData: keepPreviousData,
  });

  const errorMessage = getErrorMessage(error);

  const companies = pageData?.content || [];
  
  // UI용 페이지 번호 (1-based index로 변환)
  const currentPage = (pageData?.page.number ?? 0) + 1;
  const totalPages = pageData?.page.totalPages ?? 0;
  
  // 마지막 페이지 여부 계산 (API 기준 page 사용)
  const isLastPage = totalPages === 0 || (pageData?.page.number ?? 0) >= totalPages - 1;
  const showPagination = !!pageData && !isError; // 에러가 아닐 때만 페이지네이션 표시

  // UI에서 페이지 변경 시 호출될 함수 (1-based -> 0-based 변환)
  const handlePageChange = (newPage: number) => setPage(newPage - 1);

  return {
    companies,
    currentPage,
    totalPages,
    isLoading: isPageLoading,
    showPagination,
    isPlaceholderData,
    isLastPage,
    isError,
    errorMessage,
    sortOption,
    setPage: handlePageChange,
    setSortOption,
  };
};
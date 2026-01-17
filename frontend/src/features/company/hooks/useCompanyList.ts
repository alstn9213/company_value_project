import { keepPreviousData, useQuery } from "@tanstack/react-query";
import { companyApi } from "../api/companyApi";
import { useState } from "react";
import { AxiosError } from "axios";
import { ApiErrorData } from "../../../types/auth";
import { PageResponse, CompanySummaryResponse } from "../../../types/company";
import { getErrorMessage } from "../../../utils/errorHandler";
import { companyKeys } from "../api/queryKeys";

export interface CompanyListHookResult {
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
  const [page, setPage] = useState(0);
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
  const currentPage = pageData?.page.number ?? 0;
  const totalPages = pageData?.page.totalPages ?? 0;
  const isLastPage = totalPages === 0 || currentPage >= totalPages - 1;
  const showPagination = !!pageData && !isError; // 에러가 아닐 때만 페이지네이션 표시

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
    setPage,
    setSortOption,
  };
};
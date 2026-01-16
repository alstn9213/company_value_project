import { keepPreviousData, useQuery } from "@tanstack/react-query";
import { companyApi } from "../api/companyApi";
import { useMemo, useState } from "react";
import { AxiosError } from "axios";
import { ApiErrorData } from "../../../types/auth";
import { PageResponse, CompanySummaryResponse } from "../../../types/company";

export const useCompanyList = (pageSize: number = 12) => {
  const [page, setPage] = useState(0);
  const [sortOption, setSortOption] = useState("score");

  const {
    data: pageData,
    isLoading: isPageLoading,
    isPlaceholderData,
    isError,
    error,
  } = useQuery<PageResponse<CompanySummaryResponse>, AxiosError<ApiErrorData>>({
    queryKey: ["companies", page, sortOption],
    queryFn: () => companyApi.getAll(page, pageSize, sortOption),
    placeholderData: keepPreviousData,
  });

  // API 에러 메시지 파싱
  const errorMessage = useMemo(() => {
    if (!isError || !error) { 
      return undefined;
    }

    const responseData = error.response?.data;
    if (typeof responseData === "string") {
      return responseData;
    }
    return responseData?.message || "리스트를 불러오는 중 오류가 발생했습니다.";
  }, [isError, error]);

  // 파생 데이터 계산 (Derived State)
  const companies = pageData?.content || [];
  const currentPage = pageData?.page.number ?? 0;
  const totalPages = pageData?.page.totalPages ?? 0;

  // 마지막 페이지 여부 계산
  const isLastPage = totalPages === 0 || currentPage >= totalPages - 1;
  const showPagination = !!pageData && !isError; // 에러가 아닐 때만 페이지네이션 표시

  return {
    // Data
    companies,
    currentPage,
    totalPages,

    // Status
    isLoading: isPageLoading,
    isPlaceholderData,
    isError,
    errorMessage,
    showPagination,
    isLastPage,

    // State Values
    sortOption,

    // Handlers (State Setters)
    setPage,
    setSortOption,
  };
};
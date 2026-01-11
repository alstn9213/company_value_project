import { keepPreviousData, useQuery } from "@tanstack/react-query";
import { companyApi } from "../api/companyApi";
import { useState } from "react";

export const useCompanyList = (pageSize: number = 12) => {
  const [page, setPage] = useState(0);
  const [sortOption, setSortOption] = useState("score");

  const {
    data: pageData,
    isLoading: isPageLoading,
    isPlaceholderData,
  } = useQuery({
    queryKey: ["companies", page, sortOption],
    queryFn: () => companyApi.getAll(page, pageSize, sortOption),
    placeholderData: keepPreviousData,
  });

  // 파생 데이터 계산 (Derived State)
  const companies = pageData?.content || [];
  const currentPage = pageData?.page.number ?? 0;
  const totalPages = pageData?.page.totalPages ?? 0;
  
  // 마지막 페이지 여부 계산
  const isLastPage = totalPages === 0 || currentPage >= totalPages - 1;
  const showPagination = !!pageData;

  return {
    // Data
    companies,
    currentPage,
    totalPages,
    isPageLoading,
    isPlaceholderData,
    showPagination,
    isLastPage,
    
    // State Values
    sortOption,
    
    // Handlers (State Setters)
    setPage,
    setSortOption,
  };
};
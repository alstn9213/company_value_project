import { useState, useEffect } from "react";

interface UsePaginationProps<T> {
  data: T[] | undefined;
  itemsPerPage: number;
}

export const usePagination = <T>({ data, itemsPerPage }: UsePaginationProps<T>) => {
  const [currentPage, setCurrentPage] = useState(1);

  const totalItems = data?.length || 0;
  const totalPages = Math.ceil(totalItems / itemsPerPage);

  // 데이터가 변경되어(예: 삭제) 전체 페이지 수가 줄어들었을 때, 
  // 현재 페이지가 범위를 벗어나지 않도록 조정
  useEffect(() => {
    if (totalPages > 0 && currentPage > totalPages) {
      setCurrentPage(totalPages);
    }
  }, [totalPages, currentPage]);

  const startIndex = (currentPage - 1) * itemsPerPage;
  const paginatedData = data?.slice(startIndex, startIndex + itemsPerPage) || [];

  return {
    currentPage,
    totalPages,
    paginatedData,
    setCurrentPage,
    totalItems
  };
};
import { useState } from "react";
import { useQuery, keepPreviousData } from "@tanstack/react-query";
import { companyApi } from "../../api/companyApi";
import CompanyFilterHeader from "../../components/layout/CompanyFilterHeader";
import CompanyGridSection from "../../features/company/components/CompanyGridSection";
import Pagination from "../../components/common/Pagination";

const CompanyListPage = () => {
  const [page, setPage] = useState(0);
  const [sortOption, setSortOption] = useState("score");
  const {
    data: pageData,
    isLoading: isPageLoading,
    isPlaceholderData, // v5 변경사항: isPreviousData -> isPlaceholderData
  } = useQuery({
    queryKey: ["companies", page, sortOption],
    queryFn: () => companyApi.getAll(page, 12, sortOption),
    placeholderData: keepPreviousData, // 페이지 전환 시 깜빡임 방지
    // v5 변경사항: keepPreviousData: true -> placeholderData: keepPreviousData
  });

  // 현재 표시할 데이터 결정
  const companies = pageData?.content;
  const isLoading = isPageLoading;

  // 페이지네이션 노출 여부 조건 
  const showPagination = !!pageData;

  // 현재 페이지(number)가 (전체 페이지 수 - 1)보다 크거나 같으면 마지막 페이지
  const currentPage = pageData?.page.number ?? 0;
  const totalPages = pageData?.page.totalPages ?? 0;
  const isLastPage = totalPages === 0 || currentPage >= totalPages - 1;
  
  return (
    <div className="max-w-7xl mx-auto space-y-10 pb-10">      
      <div className="space-y-6">

        {/* 필터 및 검색 헤더 */}
        <CompanyFilterHeader
          sortOption={sortOption}
          onSortChange={setSortOption}
        />

        {/* 기업 목록 그리드 */}
        <CompanyGridSection
          isLoading={isLoading}
          companies={companies}
        />

        {/* 페이지네이션 */}
        {showPagination && (
          <Pagination
            currentPage={currentPage}
            totalPages={totalPages}
            onPageChange={setPage}
            isPlaceholderData={isPlaceholderData}
            isLastPage={isLastPage} 
          />
        )}
      </div>
    </div>
  );
};

export default CompanyListPage;

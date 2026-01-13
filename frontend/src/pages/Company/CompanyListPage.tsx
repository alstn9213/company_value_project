import { Pagination } from "../../components/common/Pagination";
import { CompanyGridSection } from "../../features/company/components/CompanyGridSection";
import { useCompanyList } from "../../features/company/hooks/useCompanyList";
import { CompanyFilterHeader } from "../../features/company/ui/CompanyFilterHeader";

const CompanyListPage = () => {
  const {
    companies,
    currentPage,
    totalPages,
    isPageLoading,
    isPlaceholderData,
    showPagination,
    isLastPage,
    sortOption,
    setPage,
    setSortOption,
  } = useCompanyList(12);
  
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
          isLoading={isPageLoading}
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

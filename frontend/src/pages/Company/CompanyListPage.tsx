import { CompanyFilterHeader } from "../../features/company/ui/p_list/CompanyFilterHeader";
import { ErrorState } from "../../components/ui/ErrorState";
import { Pagination } from "../../components/ui/Pagination";
import {  CompanyGrid, useCompanyList } from "../../features/company";

const CompanyListPage = () => {
  const {
    companies,
    currentPage,
    totalPages,
    isLoading,
    isError,
    errorMessage,
    isPlaceholderData,
    showPagination,
    isLastPage,
    sortOption,
    setPage,
    setSortOption,
  } = useCompanyList(12);

  if (isError) {
    return (
      <div className="max-w-7xl mx-auto py-10">
        <ErrorState message={errorMessage} />
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto space-y-10 pb-10">
      <div className="space-y-6">
        {/* 필터 헤더 */}
        <CompanyFilterHeader
          sortOption={sortOption}
          onSortChange={setSortOption}
        />

        {/* 기업 목록 그리드 */}
        <CompanyGrid 
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

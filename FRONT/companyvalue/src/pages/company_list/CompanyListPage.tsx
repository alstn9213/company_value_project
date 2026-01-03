import { useState, useEffect } from "react";
import { useQuery, keepPreviousData } from "@tanstack/react-query";
import { companyApi } from "../../api/companyApi";
import { ScoreResult } from "../../types/company";
import axiosClient from "../../api/axiosClient";
import TopRatedCompanies from "./components/TopRatedCompanies";
import CompanyFilterHeader from "./components/CompanyFilterHeader";
import CompanyGridSection from "./components/CompanyGridSection";

const CompanyListPage = () => {
  const [page, setPage] = useState(0);
  const [searchTerm, setSearchTerm] = useState("");
  const [debouncedSearch, setDebouncedSearch] = useState("");
  const [sortOption, setSortOption] = useState("score");

  // 검색어 디바운스 처리(입력 멈추고 0.5초 뒤 반영)
  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearch(searchTerm);
      setPage(0); // 검색 시 첫 페이지로 리셋
    }, 500);
    return () => clearTimeout(timer);
  }, [searchTerm]);

  // Top 5 우량주 데이터 조회
  const { data: topRanked } = useQuery({
    queryKey: ["topRankedCompanies"],
    queryFn: async () => {
      const res = await axiosClient.get<ScoreResult[]>("/api/scores/top");
      return res.data;
    },
    staleTime: (1000 * 60) & 5, // 5분 캐싱
  });

  // 전체 목록 쿼리 (검색어가 없을 때 실행)
  const {
    data: pageData,
    isLoading: isPageLoading,
    isPlaceholderData, // v5 변경사항: isPreviousData -> isPlaceholderData
  } = useQuery({
    queryKey: ["companies", page, sortOption],
    queryFn: () => companyApi.getAll(page, 12, sortOption),
    enabled: !debouncedSearch, // 검색어가 없을 때만 활성화
    placeholderData: keepPreviousData, // 페이지 전환 시 깜빡임 방지
    // v5 변경사항: keepPreviousData: true -> placeholderData: keepPreviousData
  });

  // 검색 쿼리 (검색어가 있을 때 실행)
  const { data: searchData, isLoading: isSearchLoading } = useQuery({
    queryKey: ["companies", "search", debouncedSearch],
    queryFn: () => companyApi.search(debouncedSearch),
    enabled: !!debouncedSearch, // 검색어가 있을 때만 활성화
  });

  // 현재 표시할 데이터 결정
  const companies = debouncedSearch
    ? searchData
    : pageData?.content;

  const isLoading = debouncedSearch
    ? isSearchLoading
    : isPageLoading;

    const showPagination = !debouncedSearch && !!pageData;

  
  return (
    <div className="max-w-7xl mx-auto space-y-10 pb-10">
      {/* Top 5 섹션 (검색 중이 아닐 때만 노출) */}
      {!debouncedSearch && topRanked && (
        <TopRatedCompanies companies={topRanked}/>
      )}

      <div className="space-y-6">
        {/* 필터 및 검색 헤더 */}
        <CompanyFilterHeader
          searchTerm={searchTerm}
          onSearchChange={setSearchTerm}
          sortOption={sortOption}
          onSortChange={setSortOption}
          isSearchActive={!!debouncedSearch}
        />
        {/* 기업 목록 그리드 및 페이지네이션 */}
        <CompanyGridSection
          isLoading={isLoading}
          companies={companies}
          // 페이지네이션 Props
          showPagination={showPagination}
          currentPage={page}
          totalPages={pageData?.totalPages || 0}
          isPlaceholderData={isPlaceholderData}
          onPageChange={setPage}
          isLastPage={pageData?.last ?? true}
        />
      </div>
    </div>
  );
};

export default CompanyListPage;

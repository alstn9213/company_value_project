import { useState, useEffect } from "react";
import { useQuery, keepPreviousData } from "@tanstack/react-query";
import { Link } from "react-router-dom";
import {
  Search,
  ChevronLeft,
  ChevronRight,
  Building2,
  Activity,
} from "lucide-react";
import { companyApi } from "../../api/companyApi";

const CompanyListPage = () => {
  const [page, setPage] = useState(0);
  const [searchTerm, setSearchTerm] = useState("");
  const [debouncedSearch, setDebouncedSearch] = useState("");

  // 검색어 디바운스 처리(입력 멈추고 0.5초 뒤 반영)
  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearch(searchTerm);
      setPage(0); // 검색 시 첫 페이지로 리셋
    }, 500);
    return () => clearTimeout(timer);
  }, [searchTerm]);

  // 1. 전체 목록 쿼리 (검색어가 없을 때 실행)
  const {
    data: pageData,
    isLoading: isPageLoading,
    isPlaceholderData, // v5 변경사항: isPreviousData -> isPlaceholderData
  } = useQuery({
    queryKey: ["companies", page],
    queryFn: () => companyApi.getAll(page),
    enabled: !debouncedSearch, // 검색어가 없을 때만 활성화
    placeholderData: keepPreviousData, // 페이지 전환 시 깜빡임 방지
    // v5 변경사항: keepPreviousData: true -> placeholderData: keepPreviousData
  });

  // 2. 검색 쿼리 (검색어가 있을 때 실행)
  const { data: searchData, isLoading: isSearchLoading } = useQuery({
    queryKey: ["companies", "search", debouncedSearch],
    queryFn: () => companyApi.search(debouncedSearch),
    enabled: !!debouncedSearch, // 검색어가 있을 때만 활성화
  });

  // 현재 표시할 데이터 결정
  const companies = debouncedSearch ? searchData : pageData?.content;
  const isLoading = debouncedSearch ? isSearchLoading : isPageLoading;
  const isEmpty = !isLoading && (!companies || companies.length === 0);
return (
    <div className="max-w-7xl mx-auto space-y-8">
      {/* 헤더 및 검색창 */}
      <div className="flex flex-col md:flex-row justify-between items-center gap-4">
        <div>
          <h1 className="text-3xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-blue-400 to-emerald-400">
            Companies
          </h1>
          <p className="text-slate-400 mt-2">
            분석하고 싶은 기업을 찾아보세요.
          </p>
        </div>

        <div className="relative w-full md:w-96">
          <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
            <Search className="h-5 w-5 text-slate-400" />
          </div>
          <input
            type="text"
            placeholder="기업명 검색 (예: Apple)"
            className="block w-full pl-10 pr-3 py-3 border border-slate-600 rounded-xl leading-5 bg-slate-800/50 text-slate-200 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 sm:text-sm transition-all backdrop-blur-sm"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
      </div>

      {/* 로딩 상태 */}
      {isLoading && (
        <div className="text-center py-20">
          <div className="inline-block animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-blue-500 mb-4"></div>
          <p className="text-slate-400">기업 목록을 불러오는 중입니다...</p>
        </div>
      )}

      {/* 데이터 없음 */}
      {isEmpty && (
        <div className="text-center py-20 bg-slate-800/30 rounded-xl border border-slate-700/50">
          <Building2 className="mx-auto h-12 w-12 text-slate-500 mb-4" />
          <p className="text-slate-300 text-lg">검색 결과가 없습니다.</p>
          <p className="text-slate-500">다른 키워드로 검색해보세요.</p>
        </div>
      )}

      {/* 기업 목록 그리드 */}
      {!isLoading && !isEmpty && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          {companies?.map((company) => (
            <Link
              key={company.ticker}
              to={`/company/${company.ticker}`}
              className="group relative bg-card border border-slate-700/50 rounded-xl p-6 hover:border-blue-500/50 transition-all duration-300 hover:transform hover:-translate-y-1 hover:shadow-xl hover:shadow-blue-500/10 overflow-hidden"
            >
              {/* 호버 효과용 배경 그라데이션 */}
              <div className="absolute inset-0 bg-gradient-to-br from-blue-600/0 via-blue-600/0 to-blue-600/0 group-hover:to-blue-600/5 transition-all duration-500" />

              <div className="relative z-10">
                <div className="flex justify-between items-start mb-4">
                  <div className="flex flex-col">
                    <span className="inline-block px-2.5 py-1 rounded-md text-xs font-semibold bg-slate-700 text-slate-300 mb-2 w-fit group-hover:bg-blue-500/20 group-hover:text-blue-300 transition-colors">
                      {company.exchange}
                    </span>
                    <h3 className="text-xl font-bold text-white group-hover:text-blue-400 transition-colors">
                      {company.ticker}
                    </h3>
                  </div>
                  <div className="p-2 bg-slate-800 rounded-lg text-slate-400 group-hover:text-blue-400 group-hover:bg-blue-500/10 transition-colors">
                    <Activity size={20} />
                  </div>
                </div>

                <div className="space-y-1">
                  <p className="text-slate-300 font-medium truncate" title={company.name}>
                    {company.name}
                  </p>
                  <p className="text-sm text-slate-500 flex items-center gap-2">
                    <Building2 size={14} />
                    {company.sector}
                  </p>
                </div>
              </div>
            </Link>
          ))}
        </div>
      )}

      {/* 페이지네이션 (검색 중이 아닐 때만 표시) */}
      {!debouncedSearch && pageData && (
        <div className="flex justify-center items-center gap-4 mt-8 pt-8 border-t border-slate-800">
          <button
            onClick={() => setPage((old) => Math.max(old - 1, 0))}
            disabled={page === 0 || isPlaceholderData} // v5 변경사항: isPreviousData -> isPlaceholderData
            className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-slate-300 bg-slate-800 border border-slate-600 rounded-lg hover:bg-slate-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            <ChevronLeft size={16} />
            이전
          </button>

          <span className="text-slate-400 text-sm">
            Page <span className="text-white font-bold">{page + 1}</span> of{" "}
            <span className="text-white font-bold">{pageData.totalPages}</span>
          </span>

          <button
            onClick={() => {
              if (!pageData.last) {
                setPage((old) => old + 1);
              }
            }}
            disabled={pageData.last || isPlaceholderData} // v5 변경사항: isPreviousData -> isPlaceholderData
            className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-slate-300 bg-slate-800 border border-slate-600 rounded-lg hover:bg-slate-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            다음
            <ChevronRight size={16} />
          </button>
        </div>
      )}
    </div>
  );
};

export default CompanyListPage;
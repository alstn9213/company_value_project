import { useState, useEffect } from "react";
import { useQuery, keepPreviousData } from "@tanstack/react-query";
import { Link } from "react-router-dom";
import {
  Search,
  ChevronLeft,
  ChevronRight,
  Building2,
  Activity,
  Trophy,
} from "lucide-react";
import { companyApi } from "../../api/companyApi";
import { ScoreResult } from "../../types/company";
import axiosClient from "../../api/axiosClient";
import { getGradeColor, getScoreColor } from "../../utils/formatters";

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
    queryKey: ["companies", page],
    queryFn: () => companyApi.getAll(page),
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
  const companies = debouncedSearch ? searchData : pageData?.content;
  const isLoading = debouncedSearch ? isSearchLoading : isPageLoading;
  const isEmpty = !isLoading && (!companies || companies.length === 0);
  return (
    <div className="max-w-7xl mx-auto space-y-10 pb-10">
      
      {/* --- [섹션 1] Top 5 추천 기업 (가로 스크롤 카드) --- */}
      {topRanked && topRanked.length > 0 && !debouncedSearch && (
        <section className="space-y-4">
          <h2 className="text-2xl font-bold text-white flex items-center gap-2 px-1">
            <Trophy className="text-yellow-400 fill-yellow-400" />
            <span className="bg-clip-text text-transparent bg-gradient-to-r from-yellow-200 to-amber-400">
              이달의 추천 우량주 Top 5
            </span>
          </h2>
          
          <div className="flex gap-5 overflow-x-auto pb-6 pt-2 px-1 scrollbar-thin scrollbar-thumb-slate-700 scrollbar-track-transparent">
            {topRanked.slice(0, 5).map((item, idx) => (
              <Link
                key={item.ticker}
                to={`/company/${item.ticker}`}
                className="min-w-[260px] bg-gradient-to-b from-slate-800 to-slate-900 border border-slate-700 rounded-2xl p-5 shadow-lg hover:-translate-y-2 transition-transform duration-300 relative group overflow-hidden"
              >
                {/* 1등 강조 효과 */}
                {idx === 0 && (
                  <div className="absolute top-0 right-0 bg-yellow-500 text-black text-xs font-bold px-3 py-1 rounded-bl-xl shadow-md z-10">
                    1st Pick
                  </div>
                )}
                
                <div className="flex justify-between items-start mb-4">
                  <div className={`w-12 h-12 rounded-xl flex items-center justify-center text-xl font-bold border-2 ${getGradeColor(item.grade)}`}>
                    {item.grade}
                  </div>
                  <div className="text-right">
                    <span className={`text-2xl font-bold ${getScoreColor(item.totalScore)}`}>
                      {item.totalScore}
                    </span>
                    <span className="text-xs text-slate-500 block">점</span>
                  </div>
                </div>

                <div className="space-y-1">
                  <h3 className="text-lg font-bold text-white truncate group-hover:text-blue-400 transition-colors">
                    {item.ticker}
                  </h3>
                  <p className="text-sm text-slate-400 truncate">{item.name}</p>
                </div>

                {/* 하단 점수 요약 바 */}
                <div className="mt-4 pt-4 border-t border-slate-700/50 flex justify-between text-xs text-slate-500">
                  <div className="flex flex-col gap-1">
                    <span>안정성</span>
                    <span className="text-slate-300">{item.stabilityScore}/40</span>
                  </div>
                  <div className="flex flex-col gap-1 text-right">
                    <span>수익성</span>
                    <span className="text-slate-300">{item.profitabilityScore}/30</span>
                  </div>
                </div>
              </Link>
            ))}
          </div>
        </section>
      )}

      {/* --- [섹션 2] 검색 및 전체 목록 --- */}
      <div className="space-y-6">
        <div className="flex flex-col md:flex-row justify-between items-end gap-4 border-b border-slate-800 pb-4">
          <div>
            <h1 className="text-3xl font-bold text-white">Companies</h1>
            <p className="text-slate-400 mt-2 text-sm">
              나스닥/뉴욕증권거래소 상장 기업 전체 리스트
            </p>
          </div>

          <div className="relative w-full md:w-80">
            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <Search className="h-4 w-4 text-slate-400" />
            </div>
            <input
              type="text"
              placeholder="티커 또는 기업명 검색..."
              className="block w-full pl-10 pr-4 py-2.5 bg-slate-900 border border-slate-700 rounded-lg text-sm text-slate-200 focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 transition-all placeholder-slate-500"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
        </div>

        {/* 로딩 상태 */}
        {isLoading && (
          <div className="text-center py-20">
            <div className="inline-block animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-blue-500 mb-4"></div>
            <p className="text-slate-400">데이터를 불러오는 중입니다...</p>
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
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-5">
            {companies?.map((company) => (
              <Link
                key={company.ticker}
                to={`/company/${company.ticker}`}
                className="group relative bg-card border border-slate-700/50 rounded-xl p-5 hover:border-blue-500/50 transition-all duration-200 hover:shadow-lg hover:bg-slate-800/80"
              >
                <div className="flex justify-between items-start mb-3">
                  <div>
                    <span className="inline-flex items-center px-2 py-0.5 rounded text-[10px] font-medium bg-slate-700 text-slate-300 mb-2">
                      {company.exchange}
                    </span>
                    <h3 className="text-lg font-bold text-white group-hover:text-blue-400 transition-colors">
                      {company.ticker}
                    </h3>
                  </div>
                  <div className="p-2 bg-slate-800/50 rounded-lg text-slate-500 group-hover:text-blue-400 transition-colors">
                    <Activity size={18} />
                  </div>
                </div>

                <div className="space-y-1">
                  <p className="text-slate-300 font-medium truncate text-sm" title={company.name}>
                    {company.name}
                  </p>
                  <p className="text-xs text-slate-500 flex items-center gap-1.5">
                    <Building2 size={12} />
                    {company.sector}
                  </p>
                </div>
              </Link>
            ))}
          </div>
        )}

        {/* 페이지네이션 (검색 중이 아닐 때만 표시) */}
        {!debouncedSearch && pageData && (
          <div className="flex justify-center items-center gap-4 mt-8 pt-8 border-t border-slate-800/50">
            <button
              onClick={() => setPage((old) => Math.max(old - 1, 0))}
              disabled={page === 0 || isPlaceholderData}
              className="flex items-center gap-1 px-3 py-1.5 text-sm font-medium text-slate-300 bg-slate-800 border border-slate-600 rounded hover:bg-slate-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              <ChevronLeft size={14} /> Prev
            </button>

            <span className="text-slate-400 text-sm font-mono">
              {page + 1} / {pageData.totalPages}
            </span>

            <button
              onClick={() => {
                if (!pageData.last) {
                  setPage((old) => old + 1);
                }
              }}
              disabled={pageData.last || isPlaceholderData}
              className="flex items-center gap-1 px-3 py-1.5 text-sm font-medium text-slate-300 bg-slate-800 border border-slate-600 rounded hover:bg-slate-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              Next <ChevronRight size={14} />
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default CompanyListPage;
import { Building2, ChevronLeft, ChevronRight } from "lucide-react";
import { Company } from "../../../types/company";
import { getGradeColor, getScoreColor } from "../../../utils/formatters";
import { Link } from "react-router-dom";
import LoadingState from "../../../components/common/LoadingState";
import EmptyState from "../../../components/common/EmptyState";

interface CompanyGridSectionProps {
  isLoading: boolean;
  companies?: Company[];
  // 페이지네이션 관련 (검색 중이 아닐 때만 사용하므로 optional)
  showPagination: boolean;
  currentPage: number;
  totalPages: number;
  isPlaceholderData: boolean;
  onPageChange: (newPage: number) => void;
  isLastPage: boolean;
}

const CompanyGridSection = ({
  isLoading,
  companies,
  showPagination,
  currentPage,
  totalPages,
  isPlaceholderData,
  onPageChange,
  isLastPage,
}: CompanyGridSectionProps) => {
  // 로딩 상태
  if (isLoading) return <LoadingState />;
  
  // 데이터 없음 상태
  if (!companies || companies.length === 0) {
    return (
      <EmptyState
        icon={Building2}
        title="검색 결과가 없습니다."
        description="다른 키워드로 검색해보세요."
      />
    );
  }

  return (
    <div className="space-y-6">
      {/* 3. 기업 목록 그리드 */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-5">
        {companies.map((company) => (
          <Link
            key={company.ticker}
            to={`/company/${company.ticker}`}
            className="group relative bg-card border border-slate-700/50 rounded-xl p-5 hover:border-blue-500/50 transition-all duration-200 hover:shadow-lg hover:bg-slate-800/80"
          >
            <div className="flex justify-between items-start mb-3">
              <div className="flex gap-2 items-start">
                <div
                  className={`w-10 h-10 rounded-lg flex items-center justify-center text-lg font-bold border ${getGradeColor(
                    company.grade
                  )}`}
                >
                  {company.grade}
                </div>
                <div>
                  <h3 className="text-lg font-bold text-white group-hover:text-blue-400 transition-colors">
                    {company.ticker}
                  </h3>
                  <span className="text-[10px] text-slate-500 bg-slate-800 px-1.5 py-0.5 rounded">
                    {company.exchange}
                  </span>
                </div>
              </div>

              <div className="text-right">
                <span
                  className={`block font-bold text-lg ${getScoreColor(
                    company.totalScore
                  )}`}
                >
                  {company.totalScore}
                </span>
              </div>
            </div>

            <div className="space-y-1 mt-2">
              <p
                className="text-slate-300 font-medium truncate text-sm"
                title={company.name}
              >
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

      {/* 4. 페이지네이션 (조건부 렌더링) */}
      {showPagination && (
        <div className="flex justify-center items-center gap-4 mt-8 pt-8 border-t border-slate-800/50">
          <button
            onClick={() => onPageChange(Math.max(currentPage - 1, 0))}
            disabled={currentPage === 0 || isPlaceholderData}
            className="flex items-center gap-1 px-3 py-1.5 text-sm font-medium text-slate-300 bg-slate-800 border border-slate-600 rounded hover:bg-slate-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            <ChevronLeft size={14} /> "Prev"
          </button>

          <span className="text-slate-400 text-sm font-mono">
            {currentPage + 1} / {totalPages}
          </span>

          <button
            onClick={() => {
              if (!isLastPage) {
                onPageChange(currentPage + 1);
              }
            }}
            disabled={isLastPage || isPlaceholderData}
            className="flex items-center gap-1 px-3 py-1.5 text-sm font-medium text-slate-300 bg-slate-800 border border-slate-600 rounded hover:bg-slate-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            "Next" <ChevronRight size={14} />
          </button>
        </div>
      )}
    </div>
  );
};

export default CompanyGridSection;
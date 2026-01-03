import { Building2 } from "lucide-react";
import { Company } from "../../../types/company";
import { getGradeColor, getScoreColor } from "../../../utils/formatters";
import { Link } from "react-router-dom";
import LoadingState from "../../../components/common/LoadingState";
import EmptyState from "../../../components/common/EmptyState";

interface CompanyGridSectionProps {
  isLoading: boolean;
  companies?: Company[];
}

const CompanyGridSection = ({
  isLoading,
  companies,
}: CompanyGridSectionProps) => {
  // 로딩 상태
  if (isLoading) return <LoadingState />;
  
  // 데이터 없음 상태
  if (!companies || companies.length === 0) {
    return (
      <EmptyState
        title="검색 결과가 없습니다."
        description="다른 키워드로 검색해보세요."
      />
    );
  }

  return (
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

  );
};

export default CompanyGridSection;
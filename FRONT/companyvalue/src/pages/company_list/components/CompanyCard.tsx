import { Link } from "react-router-dom";
import { Building2 } from "lucide-react";
import { Company } from "../../../types/company";
import { getGradeColor, getScoreColor } from "../../../utils/formatters";

interface CompanyCardProps {
  company: Company;
  className?: string; // 스타일 오버라이딩용
}

const CompanyCard = ({ company, className = "" }: CompanyCardProps) => {
  const hasDetails = 'sector' in company && 'exchange' in company;

  return (
    <Link
      to={`/company/${company.ticker}`}
      className={`group relative bg-card border border-slate-700/50 rounded-xl p-5 hover:border-blue-500/50 transition-all duration-200 hover:shadow-lg hover:bg-slate-800/80 overflow-hidden ${className}`}
    >
      <div className="flex justify-between items-start mb-3">
        <div className="flex gap-2 items-start">
          {/* 등급 뱃지 */}
          <div className={`w-10 h-10 rounded-lg flex items-center justify-center text-lg font-bold border ${getGradeColor(company.grade)}`}>
            {company.grade}
          </div>
          <div className="overflow-hidden">
            {/* 티커 (심볼) */}
            <h3 className="text-lg font-bold text-white group-hover:text-blue-400 transition-colors">
              {company.ticker}
            </h3>

            {/* 거래소 정보 (데이터가 있을 때만 표시) */}
            {hasDetails && (
              <span className="text-[10px] text-slate-500 bg-slate-800 px-1.5 py-0.5 rounded">
                {(company as Company).exchange}
              </span>
            )}
          </div>
        </div>

        {/* 종합 점수 */}
        <div className="text-right">
          <span className={`block font-bold text-lg ${getScoreColor(company.totalScore)}`}>
            {company.totalScore}
          </span>
        </div>
      </div>

      <div className="space-y-1 mt-2">
        {/* 회사명 */}
        <p className="text-slate-300 font-medium truncate text-sm" title={company.name}>
          {company.name}
        </p>

        {/* 섹터 정보 (데이터가 있을 때만 표시) */}
        {hasDetails && (
          <p className="text-xs text-slate-500 flex items-center gap-1.5">
            <Building2 size={12} />
            {(company as Company).sector}
          </p>
        )}
      </div>
    </Link>
  );
};

export default CompanyCard;
import { HelpCircle, TrendingUp } from "lucide-react";
import { FINANCIAL_TERMS, TermDefinition } from "../constants/financialTerms";
import { formatCurrency } from "../../../utils/formatters";

interface FinancialData {
  revenue: number;
  operatingProfit: number;
  netIncome: number;
  operatingCashFlow: number;
  totalAssets: number;
  totalLiabilities: number;
  totalEquity: number;
  researchAndDevelopment: number;
  capitalExpenditure: number;
  year: number;
  quarter: number;
}

interface Props {
  financial: FinancialData;
}

const FinancialSummary = ({ financial }: Props) => {
  if (!financial) {
    return (
      <div className="bg-card border border-slate-700/50 rounded-xl p-6 h-full flex items-center justify-center text-slate-500">
        재무 데이터가 없습니다.
      </div>
    );
  }

  return (
    <div className="bg-card border border-slate-700/50 rounded-xl p-6 shadow-lg backdrop-blur-sm">
      <h3 className="text-lg font-bold text-white mb-6 flex items-center gap-2">
        <TrendingUp size={20} className="text-blue-400" />
        재무 요약 ({financial.year}년 {financial.quarter}분기 기준)
      </h3>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-x-8 gap-y-8">
        {/* 1. 손익계산서 (Income Statement) */}
        <div className="space-y-4">
          <h4 className="text-sm font-bold text-slate-400 uppercase tracking-wider border-b border-slate-800 pb-2">
            손익계산서
          </h4>
          <FinancialRow
            label="매출액"
            value={financial.revenue}
            term={FINANCIAL_TERMS.revenue}
            isMain
          />
          <FinancialRow
            label="영업이익"
            value={financial.operatingProfit}
            term={FINANCIAL_TERMS.operatingProfit}
            highlight
          />
          <FinancialRow
            label="당기순이익"
            value={financial.netIncome}
            term={FINANCIAL_TERMS.netIncome}
          />
          <FinancialRow
            label="R&D 투자"
            value={financial.researchAndDevelopment}
            term={FINANCIAL_TERMS.researchAndDevelopment}
          />
        </div>

        {/* 2. 재무상태표 (Balance Sheet) */}
        <div className="space-y-4">
          <h4 className="text-sm font-bold text-slate-400 uppercase tracking-wider border-b border-slate-800 pb-2">
            재무상태표
          </h4>
          <FinancialRow
            label="자산 총계"
            value={financial.totalAssets}
            term={FINANCIAL_TERMS.totalAssets}
          />
          <FinancialRow
            label="부채 총계"
            value={financial.totalLiabilities}
            term={FINANCIAL_TERMS.totalLiabilities}
            color="text-red-400"
          />
          <FinancialRow
            label="자본 총계"
            value={financial.totalEquity}
            term={FINANCIAL_TERMS.totalEquity}
            color="text-blue-400"
          />
        </div>

        {/* 3. 현금흐름표 (Cash Flow) */}
        <div className="space-y-4">
          <h4 className="text-sm font-bold text-slate-400 uppercase tracking-wider border-b border-slate-800 pb-2">
            현금흐름
          </h4>
          <FinancialRow
            label="영업활동 현금흐름"
            value={financial.operatingCashFlow}
            term={FINANCIAL_TERMS.operatingCashFlow}
            highlight
          />
          <FinancialRow
            label="설비 투자 (CapEx)"
            value={financial.capitalExpenditure}
            term={FINANCIAL_TERMS.capitalExpenditure}
          />
        </div>
      </div>
    </div>
  );
};

const FinancialRow = ({
  label,
  value,
  term,
  isMain = false,
  highlight = false,
  color,
}: {
  label: string;
  value: number;
  term?: TermDefinition;
  isMain?: boolean;
  highlight?: boolean;
  color?: string;
}) => {
  return (
    <div className="flex justify-between items-center group relative">
      {/* 라벨 + 툴팁 트리거 */}
      <div className="flex items-center gap-1.5 cursor-help">
        <span
          className={`
            border-b border-dotted border-slate-600 transition-colors
            ${isMain ? "text-white font-bold" : "text-slate-400"}
            ${highlight ? "text-slate-200" : ""}
            group-hover:border-slate-400 group-hover:text-white
          `}
        >
          {label}
        </span>
        <HelpCircle
          size={12}
          className="text-slate-600 group-hover:text-slate-400 transition-colors"
        />
      </div>

      {/* 값 표시 */}
      <span
        className={`font-mono tracking-tight ${
          isMain ? "text-lg font-bold" : "text-base"
        } ${color ? color : "text-slate-200"}`}
      >
        {formatCurrency(value)}
      </span>

      {/* 툴팁 (Hover 시 등장) - ScoreAnalysis와 동일한 스타일 */}
      {term && (
        <div className="absolute bottom-full left-0 mb-2 w-72 p-4 bg-slate-900/95 border border-slate-700 rounded-lg shadow-xl backdrop-blur-md z-50 hidden group-hover:block animate-in fade-in zoom-in-95 duration-200 pointer-events-none">
          <h4 className="font-bold text-slate-100 mb-2 text-sm">
            {term.title}
          </h4>
          <p className="text-xs text-slate-300 leading-relaxed whitespace-pre-line">
            {term.description}
          </p>
          {/* 말풍선 꼬리 */}
          <div className="absolute top-full left-6 -mt-1.5 border-4 border-transparent border-t-slate-700" />
        </div>
      )}
    </div>
  );
};

export default FinancialSummary;

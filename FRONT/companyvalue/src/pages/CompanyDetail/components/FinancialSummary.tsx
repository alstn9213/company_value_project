import { HelpCircle } from "lucide-react";
import { useState } from "react";
import { formatCurrency } from "../../../utils/formatters";
import TermHelpModal from "./TermHelpModal";
import { FINANCIAL_TERMS } from "../constants/financialTerms";

interface FinancialData {
  year: number;
  quarter: number;
  revenue: number;
  operatingProfit: number;
  netIncome: number;
  operatingCashFlow: number;
  totalAssets: number;
  totalLiabilities: number;
  totalEquity: number;
  researchAndDevelopment: number;
  capitalExpenditure: number;
}

interface Props {
  financial: FinancialData;
}

const FinancialSummary = ({financial}: Props) => {
  const [selectedTermKey, setSelectedTermKey] = useState<string | null>(null);

  const FinancialCard = ({
    label,
    value,
    termKey,
    color = "text-slate-200",
    isHighlight = false,
  }: {
    label: string;
    value: number;
    termKey: keyof typeof FINANCIAL_TERMS;
    color?: string;
    isHighlight?: boolean;
  }) => (
    <div
      onClick={() => setSelectedTermKey(termKey as string)}
      className={`relative p-4 rounded-xl border border-transparent transition-all duration-200 cursor-pointer group
        ${
          isHighlight
            ? "bg-slate-800/80 hover:bg-slate-700 hover:border-slate-600"
            : "bg-slate-800/30 hover:bg-slate-800 hover:border-slate-700"
        }
      `}
    >
      <div className="flex justify-between items-start mb-1">
        <div className="flex items-center gap-1.5 text-slate-400 text-sm group-hover:text-white transition-colors">
          {label}
          {/* 호버 시 물음표 아이콘 등장 */}
          <HelpCircle
            size={14}
            className="opacity-0 -translate-y-1 group-hover:translate-y-0 group-hover:opacity-100 transition-all duration-300 text-emerald-400"
          />
        </div>
      </div>
      <div className={`text-lg font-mono font-bold tracking-tight ${color}`}>
        {formatCurrency(value)}
      </div>
      
      {/* 클릭 유도 힌트 (모바일 등에서 유용) */}
      <div className="absolute top-2 right-2 opacity-0 group-hover:opacity-100 transition-opacity text-[10px] text-slate-500 bg-slate-900/80 px-1.5 py-0.5 rounded border border-slate-700 pointer-events-none">
        클릭해서 설명보기
      </div>
    </div>
  );

  return (
    <>
      <div className="bg-card border border-slate-700/50 rounded-xl p-6 shadow-lg backdrop-blur-sm h-full">
        <div className="flex justify-between items-end mb-6 border-b border-slate-800 pb-4">
          <div>
            <h3 className="text-lg font-bold text-white flex items-center gap-2">
              📊 최신 재무제표
            </h3>
            <p className="text-slate-500 text-xs mt-1">
              {financial.year}년 {financial.quarter}분기 기준 (단위: USD)
            </p>
          </div>
          <span className="text-xs text-emerald-400/80 bg-emerald-400/10 px-2 py-1 rounded-full border border-emerald-400/20">
            Tip: 항목을 클릭해보세요
          </span>
        </div>

        <div className="space-y-6">
          {/* 1. 손익 계산서 (수익성) */}
          <div>
            <h4 className="text-xs font-bold text-slate-500 uppercase mb-3 pl-1">Profitability (수익성)</h4>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <FinancialCard
                label="매출액"
                value={financial.revenue}
                termKey="revenue"
                color="text-white"
                isHighlight
              />
              <FinancialCard
                label="영업이익"
                value={financial.operatingProfit}
                termKey="operatingProfit"
                color="text-blue-300"
                isHighlight
              />
              <FinancialCard
                label="당기순이익"
                value={financial.netIncome}
                termKey="netIncome"
                color="text-emerald-300"
              />
              <FinancialCard
                label="영업현금흐름"
                value={financial.operatingCashFlow}
                termKey="operatingCashFlow"
                color="text-yellow-300"
              />
            </div>
          </div>

          {/* 구분선 */}
          <div className="h-px bg-slate-800 w-full" />

          {/* 2. 재무상태표 (안정성) */}
          <div>
            <h4 className="text-xs font-bold text-slate-500 uppercase mb-3 pl-1">Financial Position (재무상태)</h4>
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
              <FinancialCard
                label="자산 총계"
                value={financial.totalAssets}
                termKey="totalAssets"
              />
              <FinancialCard
                label="부채 총계"
                value={financial.totalLiabilities}
                termKey="totalLiabilities"
                color="text-red-300"
              />
              <FinancialCard
                label="자본 총계"
                value={financial.totalEquity}
                termKey="totalEquity"
                color="text-indigo-300"
              />
            </div>
          </div>

          {/* 3. 투자 활동 (미래 성장) */}
          <div>
            <h4 className="text-xs font-bold text-slate-500 uppercase mb-3 pl-1">Investment (미래투자)</h4>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <FinancialCard
                label="R&D 투자비용"
                value={financial.researchAndDevelopment}
                termKey="researchAndDevelopment"
                color="text-purple-300"
              />
              <FinancialCard
                label="설비 투자(CapEx)"
                value={financial.capitalExpenditure}
                termKey="capitalExpenditure"
                color="text-orange-300"
              />
            </div>
          </div>
        </div>
      </div>

      {/* 용어 설명 모달 연결 */}
      <TermHelpModal
        termKey={selectedTermKey}
        onClose={() => setSelectedTermKey(null)}
      />
    </>
  );
};

export default FinancialSummary;
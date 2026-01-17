import { FileBarChart2, TrendingUp } from "lucide-react";
import { EmptyState } from "../../../components/ui/EmptyState";
import { FinancialStatementResponse } from "../../../types/company";
import { IncomeStatement } from "../ui/p_detail/financial/IncomeStatement";
import { BalanceSheet } from "../ui/p_detail/financial/BalanceSheet";
import { CashFlowStatement } from "../ui/p_detail/financial/CashFlowStatement";
import { Skeleton } from "../../../components/ui/Skeleton";

interface FinancialSummaryProps {
  financial: FinancialStatementResponse | undefined;
  isLoading: boolean;
}

export const FinancialSummary = ({ financial, isLoading }: FinancialSummaryProps) => {

  if (isLoading) {
    return (
      <Skeleton/>
    );
  }
 
  if (!financial) {
    return (
      <div className="bg-card border border-slate-700/50 rounded-xl p-6 h-full flex items-center justify-center">
        <EmptyState 
          icon={<FileBarChart2 size={48} className="text-slate-600 mb-4" />}
          title="재무 데이터 없음"
          description="해당 기업의 재무 정보를 불러올 수 없습니다."
        />
      </div>
    );
  }

  return (
    <div className="bg-card border border-slate-700/50 rounded-xl p-6 shadow-lg backdrop-blur-sm">
      {/* 헤더 */}
      <h3 className="text-lg font-bold text-white mb-6 flex items-center gap-2">
        <TrendingUp size={20} className="text-blue-400" />
        재무 요약 ({financial.year}년 {financial.quarter}분기 기준)
      </h3>

      {/* 재무 제표 그리드 */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-x-8 gap-y-8">
        <IncomeStatement data={financial} />
        <BalanceSheet data={financial} />
        <CashFlowStatement data={financial} />
      </div>
    </div>
  );
};


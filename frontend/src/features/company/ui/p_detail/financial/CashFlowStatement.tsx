import { FinancialStatementResponse } from "../../../../../types/company";
import { FINANCIAL_TERMS } from "../constants/financialTerms";
import { FinancialRow } from "./FinancialRow";

interface CashFlowStatementProps {
  data: FinancialStatementResponse;
}

export const CashFlowStatement = ({ data }: CashFlowStatementProps) => {
  return (
    <div className="space-y-4">
      <h4 className="text-sm font-bold text-slate-400 uppercase tracking-wider border-b border-slate-800 pb-2">
        현금흐름
      </h4>
      <FinancialRow
        label="영업활동 현금흐름"
        value={data.operatingCashFlow}
        term={FINANCIAL_TERMS.operatingCashFlow}
      />
      <FinancialRow
        label="설비 투자 (CapEx)"
        value={data.capitalExpenditure}
        term={FINANCIAL_TERMS.capitalExpenditure}
      />
    </div>
  );
};

import { FinancialStatementResponse } from "../../../../../types/company";
import { FINANCIAL_TERMS } from "../../../constants/financialTerms";
import { FinancialRow } from "./FinancialRow";

interface IncomeStatementProps {
  data: FinancialStatementResponse;
}

export const IncomeStatement = ({ data }:IncomeStatementProps) => {
  return (
    <div className="space-y-4">
      <h4 className="text-sm font-bold text-slate-400 uppercase tracking-wider border-b border-slate-800 pb-2">
        손익계산서
      </h4>
      <FinancialRow
        label="매출액"
        value={data.revenue}
        term={FINANCIAL_TERMS.revenue}
      />
      <FinancialRow
        label="영업이익"
        value={data.operatingProfit}
        term={FINANCIAL_TERMS.operatingProfit}
      />
      <FinancialRow
        label="당기순이익"
        value={data.netIncome}
        term={FINANCIAL_TERMS.netIncome}
      />
      <FinancialRow
        label="R&D 투자"
        value={data.researchAndDevelopment}
        term={FINANCIAL_TERMS.researchAndDevelopment}
      />
    </div>
  );
};

import { FinancialData } from "../../../../types/company";
import { FINANCIAL_TERMS } from "../../constants/financialTerms";
import { FinancialRow } from "../rows/FinancialRow";

interface Props {
  data: FinancialData;
}

export const IncomeStatement = ({ data }: Props) => {
  return (
    <div className="space-y-4">
      <h4 className="text-sm font-bold text-slate-400 uppercase tracking-wider border-b border-slate-800 pb-2">
        손익계산서
      </h4>
      <FinancialRow
        label="매출액"
        value={data.revenue}
        term={FINANCIAL_TERMS.revenue}
        isMain
      />
      <FinancialRow
        label="영업이익"
        value={data.operatingProfit}
        term={FINANCIAL_TERMS.operatingProfit}
        highlight
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

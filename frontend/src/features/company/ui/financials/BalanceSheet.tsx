import { FinancialData } from "../../../../types/company";
import { FINANCIAL_TERMS } from "../../constants/financialTerms";
import { FinancialRow } from "../rows/FinancialRow";

interface Props {
  data: FinancialData;
}

export const BalanceSheet = ({ data }: Props) => {
  return (
    <div className="space-y-4">
      <h4 className="text-sm font-bold text-slate-400 uppercase tracking-wider border-b border-slate-800 pb-2">
        재무상태표
      </h4>
      <FinancialRow
        label="자산 총계"
        value={data.totalAssets}
        term={FINANCIAL_TERMS.totalAssets}
      />
      <FinancialRow
        label="부채 총계"
        value={data.totalLiabilities}
        term={FINANCIAL_TERMS.totalLiabilities}
        color="text-red-400"
      />
      <FinancialRow
        label="자본 총계"
        value={data.totalEquity}
        term={FINANCIAL_TERMS.totalEquity}
        color="text-blue-400"
      />
    </div>
  );
};

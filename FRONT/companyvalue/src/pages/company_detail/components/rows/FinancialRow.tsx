import { formatCurrency } from "../../../../utils/formatters";
import TermWithTooltip from "../../../../components/common/TermWithTooltip";
import { TermDefinition } from "../../constants/financialTerms";

interface Props {
  label: string;
  value: number;
  term?: TermDefinition;
  isMain?: boolean;
  highlight?: boolean;
  color?: string;
}

const FinancialRow = ({
  label,
  value,
  term,
  isMain = false,
  highlight = false,
  color,
}: Props) => {
  const labelStyle = `
    ${isMain ? "text-white font-bold" : "text-slate-400"}
    ${highlight ? "text-slate-200" : ""}
  `;

  return (
    <div className="flex justify-between items-center group relative">
      <TermWithTooltip 
        label={label} 
        term={term} 
        className={labelStyle}
      />
      <span
        className={`font-mono tracking-tight ${
          isMain ? "text-lg font-bold" : "text-base"
        } ${color ? color : "text-slate-200"}`}
      >
        {formatCurrency(value)}
      </span>
    </div>
  );
};

export default FinancialRow;
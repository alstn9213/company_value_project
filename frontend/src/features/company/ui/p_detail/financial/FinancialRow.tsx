import { formatCurrency } from "../../../../../utils/formatters";
import { TermDefinition } from "../../../../../types/term";
import { TermWithTooltip } from "../../../../../components/ui/TermWithTooltip";

interface FinancialRowProps {
  label: string;
  value: number;
  term?: TermDefinition;
  color?: string;
}

export const FinancialRow = ({
  label,
  value,
  term,
  color = "text-slate-200",
}: FinancialRowProps) => {
  return (
    <div className="flex justify-between items-center group relative">
      <TermWithTooltip 
        label={label} 
        term={term} 
      />
      <span className={`font-mono tracking-tight ${color}`}>
        {formatCurrency(value)}
      </span>
    </div>
  );
};

import { TermWithTooltip } from "../../../../components/ui/TermWithTooltip";
import { TermDefinition } from "../../../../types/term";

interface Props {
  label: string;
  value: number;
  max: number;
  color?: string;
  term?: TermDefinition;
}

export const ScoreRow = ({
  label,
  value,
  max,
  color = "bg-blue-500",
  term,
}: Props) => {
  return (
    <div className="flex flex-col gap-1.5 group relative">
      <div className="flex justify-between items-center text-xs">

        <TermWithTooltip 
          label={label} 
          term={term} 
          className="font-medium text-slate-400"
        />

        <span className="text-slate-300 font-mono">
          <span className="text-white font-bold">{value}</span> / {max}
        </span>

      </div>

      <div className="w-full h-2 bg-slate-800 rounded-full overflow-hidden">
        <div
          className={`h-full rounded-full transition-all duration-1000 ease-out ${color}`}
          style={{ width: `${Math.min((value / max) * 100, 100)}%` }}
        />
      </div>
      
    </div>
  );
};

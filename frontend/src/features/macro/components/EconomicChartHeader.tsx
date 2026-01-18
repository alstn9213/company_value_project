import { useMacroLatest } from "../hooks/useMacroQueries";
import { Skeleton } from "../../../components/ui/Skeleton";

export const EconomicChartHeader = () => {
  const { macroData, isLoading } = useMacroLatest();

  if (isLoading) {
    return ( 
      <Skeleton className="w-32 h-5 rounded bg-slate-800" />
    );
  }

  if (!macroData) {
    return null;
  }

  return (
    <div className="flex items-end justify-between">
      <h2 className="text-2xl font-bold text-slate-100">미국의 경제 상황</h2>     
        <span className="text-sm text-slate-400">
          {macroData.date ? `기준일 ${macroData.date}` : '-'}
        </span>
    </div>
  );
};
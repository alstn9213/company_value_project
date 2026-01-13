import { Skeleton } from "../../../components/ui/Skeleton";

interface EconomicChartHeaderProps {
  latestDate?: string;
  isLoading: boolean;
}

export const EconomicChartHeader = ({ latestDate, isLoading }: EconomicChartHeaderProps) => {
  if (isLoading) {
    return ( 
      <Skeleton className="w-32 h-5 rounded bg-slate-800" />
    );
  }

  return (
    <div className="flex items-end justify-between">
      <h2 className="text-2xl font-bold text-slate-100">미국의 경제 상황</h2>     
        <span className="text-sm text-slate-400">
          {latestDate ? `기준일 ${latestDate}` : '-'}
        </span>
    </div>
  );
};
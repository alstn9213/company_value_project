import { MacroData } from "../../../types/macro";
import { Skeleton } from "../../../components/ui/Skeleton";
import { EconomicChart } from "./EconomicChart";
import { MarketGuide } from "../ui/guides/MarketGuide";
import { ChartGuide } from "../ui/guides/ChartGuide";

interface EconomicChartSectionProps {
  latestDate?: string; 
  history?: MacroData[]; 
  isLoading: boolean;
}

export const EconomicChartSection = ({ latestDate, history, isLoading }: EconomicChartSectionProps) => {

  return (
    <section className="flex flex-col gap-6 xl:col-span-7">
      {/* 헤더 영역 */}
      <div className="flex items-end justify-between">
        <h2 className="text-2xl font-bold text-slate-100">미국의 경제 상황</h2>
        {/* 날짜 */}
        {isLoading ? (
          <Skeleton className="w-32 h-5 rounded bg-slate-800" />
        ) : (
          <span className="text-sm text-slate-400">기준일 {latestDate}</span>
        )}
      </div>

      {/* 메인 차트 영역 */}
      <EconomicChart history={history} isLoading={isLoading} />

      {/* 가이드 섹션 */}
      <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
        <MarketGuide />
        <ChartGuide />
      </div>
    </section>
    );
};

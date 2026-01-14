import { MacroDataResponse } from "../../../types/macro";
import { EconomicChart } from "../components/EconomicChart";
import { MarketGuide } from "../ui/guides/MarketGuide";
import { ChartGuide } from "../ui/guides/ChartGuide";
import { EconomicChartHeader } from "../components/EconomicChartHeader";

interface EconomicChartSectionProps {
  latestDate?: string; 
  history?: MacroDataResponse[]; 
  isLoading: boolean;
}

export const EconomicChartSection = ({ latestDate, history, isLoading }: EconomicChartSectionProps) => {

  return (
    <section className="flex flex-col gap-6 xl:col-span-7">
      {/* 헤더 영역 */}
      <EconomicChartHeader 
        latestDate={latestDate} 
        isLoading={isLoading} 
      />

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

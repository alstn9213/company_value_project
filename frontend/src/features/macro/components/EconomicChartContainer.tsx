import { EconomicChart } from "./EconomicChart";
import { MarketGuide } from "../ui/guides/MarketGuide";
import { ChartGuide } from "../ui/guides/ChartGuide";

export const EconomicChartContainer = () => {
  return (
    <section className="flex flex-col gap-6 xl:col-span-7">

      {/* 메인 차트 영역 */}
      <EconomicChart />

      {/* 가이드 섹션 */}
      <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
        <MarketGuide />
        <ChartGuide />
      </div>
    </section>
    );
};

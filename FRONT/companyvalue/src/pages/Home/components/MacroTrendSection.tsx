import { MacroData } from "../../../types/macro";
import ChartGuide from "./ChartGuide";
import EconomicChart from "./EconomicChart";
import MarketGuide from "./MarketGuide";

interface MacroTrendSectionProps {
  latestDate: string;
  history: MacroData[];
}

const MacroTrendSection = ({ latestDate, history }: MacroTrendSectionProps) => {
    return(
      <section className="flex flex-col gap-6 xl:col-span-7">
        
        <div className="flex items-end justify-between">
          <h2 className="text-2xl font-bold text-slate-100">
            미국의 경제 상황
          </h2>
          <span className="text-sm text-slate-400">
            기준일 {latestDate}
          </span>
        </div>

        {/* 메인 차트 */}
        <EconomicChart history={history}/>

        {/* 가이드 섹션: 정보(MarketGuide)와 차트범례(ChartGuide)를 나란히 배치 */}
        <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
          <MarketGuide />
          <ChartGuide />
        </div>

      </section>
    )
}

export default MacroTrendSection;
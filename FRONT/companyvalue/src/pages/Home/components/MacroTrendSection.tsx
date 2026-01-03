import { MacroData } from "../../../types/macro";
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
          <EconomicChart history={history}/>
          <MarketGuide/>
        </section>
    )
}

export default MacroTrendSection;
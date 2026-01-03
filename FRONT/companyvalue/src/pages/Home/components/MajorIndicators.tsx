import { Activity, DollarSign, Percent, TrendingDown } from "lucide-react";
import IndicatorItem from "./IndicatorItem";
import { MacroData } from "../../../types/macro";

interface MajorIndicatorsProps {
  latest: MacroData;
}

const MajorIndicators = ({ latest }: MajorIndicatorsProps) => {
    return(
        <section className="space-y-4 xl:col-span-2">
        <h2 className="text-lg font-bold text-slate-100 border-l-4 border-blue-500 pl-3">
            주요 지표
        </h2>
        <div className="flex flex-col gap-3">
            <IndicatorItem
            label="기준 금리"
            value={`${latest.fedFundsRate}%`}
            icon={<DollarSign size={16} />}
            color="text-slate-200"
            />
            {/* (4.0% 이상이면 경고색) */}
            <IndicatorItem
            label="10년 물 금리"
            value={`${latest.us10y}%`}
            icon={<Activity size={16} />}
            color={latest.us10y >= 4.0 ? "text-orange-400" : "text-blue-400"} 
            />
            <IndicatorItem
            label="2년 물 금리"
            value={`${latest.us2y}%`}
            icon={<Activity size={16} />}
            color="text-emerald-400"
            />
            <IndicatorItem
            label="장단기 금리 역전"
            value={`${latest.spread} %p`}
            icon={<TrendingDown size={16} />}
            color={latest.spread < 0 ? "text-red-500" : "text-slate-400"}
            isAlert={latest.spread < 0}
            />
            <IndicatorItem
            label="인플레이션"
            value={`${latest.inflation}%`}
            icon={<Percent size={16} />}
            color="text-red-400"
            />
        </div>
        </section>
    );
}

export default MajorIndicators;
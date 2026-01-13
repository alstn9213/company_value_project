import { Activity, DollarSign, Percent, TrendingDown } from "lucide-react";
import { MacroDataResponse } from "../../../types/macro";
import { IndicatorItem } from "./IndicatorItem";
import { MajorIndicatorSkeleton } from "./skeleton/MajorIndicatorSkeleton";

interface IndicatorGridProps {
  data?: MacroDataResponse;
  isLoading: boolean;
}

export const IndicatorGrid = ({ data, isLoading }: IndicatorGridProps) => {
  if (isLoading) {
    <MajorIndicatorSkeleton/>
  }

  return (
    <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
    <IndicatorItem
      label="기준 금리"
      value={`${data?.fedFundsRate}%`}
      icon={<DollarSign size={16} />}
      color="text-slate-200"
    />
    <IndicatorItem
      label="10년 물 금리"
      value={`${data?.us10y}%`}
      icon={<Activity size={16} />}
      color={
        data?.us10y >= 4.0 ? "text-orange-400" : "text-blue-400"
      }
    />
    <IndicatorItem
      label="2년 물 금리"
      value={`${data?.us2y}%`}
      icon={<Activity size={16} />}
      color="text-emerald-400"
    />
    <IndicatorItem
      label="장단기 금리 역전"
      value={`${data?.spread} %p`}
      icon={<TrendingDown size={16} />}
      color={data?.spread < 0 ? "text-red-500" : "text-slate-400"}
      isAlert={data?.spread < 0}
    />
    <IndicatorItem
      label="인플레이션"
      value={`${data?.inflation}%`}
      icon={<Percent size={16} />}
      color="text-red-400"
    />
    </div>
  )
}
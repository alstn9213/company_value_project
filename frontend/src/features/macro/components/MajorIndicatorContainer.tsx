import { Activity, DollarSign, Percent, TrendingDown } from "lucide-react";
import { IndicatorItem } from "../ui/IndicatorItem";
import { MajorIndicatorSkeleton } from "../ui/skeleton/MajorIndicatorSkeleton";
import { useMacroLatest } from "../hooks/useMacroQueries";

export const MajorIndicatorContainer = () => {
  const { macroData, isLoading } = useMacroLatest();
  if (isLoading) {
    return <MajorIndicatorSkeleton/>
  }
  
  if (!macroData) {
    return null;
  }

  return (
    <section className="space-y-4 xl:col-span-2">
      {/* 공통 헤더 영역 */}
      <h2 className="text-lg font-bold text-slate-100 border-l-4 border-blue-500 pl-3">주요 지표</h2>

      <div className="flex flex-col gap-3">
        <IndicatorItem
          label="기준 금리"
          value={`${macroData.fedFundsRate}%`}
          icon={<DollarSign size={16} />}
          color="text-slate-200"
        />
        <IndicatorItem
          label="10년 물 금리"
          value={`${macroData.us10y}%`}
          icon={<Activity size={16} />}
          color={
            macroData.us10y >= 4.0 ? "text-orange-400" : "text-blue-400"
          }
        />
        <IndicatorItem
          label="2년 물 금리"
          value={`${macroData.us2y}%`}
          icon={<Activity size={16} />}
          color="text-emerald-400"
        />
        <IndicatorItem
          label="장단기 금리 역전"
          value={`${macroData.spread} %p`}
          icon={<TrendingDown size={16} />}
          color={macroData.spread < 0 ? "text-red-500" : "text-slate-400"}
          isAlert={macroData.spread < 0}
        />
        <IndicatorItem
          label="인플레이션"
          value={`${macroData.inflation}%`}
          icon={<Percent size={16} />}
          color="text-red-400"
        />
      </div>
    </section>
  );
};

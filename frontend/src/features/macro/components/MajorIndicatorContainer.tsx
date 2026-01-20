import { Activity, DollarSign, Percent, TrendingDown } from "lucide-react";
import { IndicatorItem } from "../ui/IndicatorItem";
import { MajorIndicatorSkeleton } from "../ui/skeleton/MajorIndicatorSkeleton";
import { useMacroLatest } from "../hooks/useMacroQueries";
import { EmptyState } from "../../../components/ui/EmptyState";
import { ErrorState } from "../../../components/ui/ErrorState";

export const MajorIndicatorContainer = () => {
  const { macroData, isLoading, isError, refetch } = useMacroLatest();
  
  if (isLoading) {
    return <MajorIndicatorSkeleton className="xl:col-span-2" />
  }

  if (isError) {
    return (
      <section className="space-y-4 xl:col-span-2">
        <h2 className="text-lg font-bold text-slate-100 border-l-4 border-blue-500 pl-3">
          주요 지표
        </h2>
        <div className="bg-slate-800 rounded-xl min-h-[300px] flex items-center justify-center">
            <ErrorState 
              title="지표를 불러올 수 없습니다"
              message="데이터 로딩 중 오류가 발생했습니다."
              onRetry={refetch} 
            />
        </div>
      </section>
    );
  }
  
  if (!macroData) {
    return (
      <section className="space-y-4 xl:col-span-2">
        <h2 className="text-lg font-bold text-slate-100 border-l-4 border-blue-500 pl-3">
          주요 지표
        </h2>
        <div className="flex h-full min-h-[300px] items-center justify-center rounded-lg bg-slate-800/50 p-4">
          <EmptyState
            title="데이터 없음"
            description="현재 표시할 주요 지표 데이터가 없습니다."
          />
        </div>
      </section>
    );
  }

  return (
    <section className="space-y-4 xl:col-span-2">
      {/* 공통 헤더 영역 */}
      <h2 className="text-lg font-bold text-slate-100 border-l-4 border-blue-500 pl-3">
        주요 지표
      </h2>

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

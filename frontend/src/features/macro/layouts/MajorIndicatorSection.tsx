import { Activity, AlertCircle, DollarSign, Percent, TrendingDown } from "lucide-react";
import { MacroDataResponse } from "../../../types/macro";
import { Skeleton } from "../../../components/ui/Skeleton";
import { IndicatorItem } from "../ui/IndicatorItem";
import { EmptyState } from "../../../components/ui/EmptyState";

interface MajorIndicatorsProps {
  latest?: MacroDataResponse;
  isLoading: boolean;
}

export const MajorIndicatorSection = ({ latest, isLoading }: MajorIndicatorsProps) => {
  return (
    <section className="space-y-4 xl:col-span-2">
      {/* 공통 헤더 영역 */}
      <h2 className="text-lg font-bold text-slate-100 border-l-4 border-blue-500 pl-3">주요 지표</h2>

      {/* 콘텐츠 영역 (로딩 상태에 따라 분기) */}
      <div className="flex flex-col gap-3">
        {isLoading ? (
          // 로딩 중일 때
          [1, 2, 3, 4, 5].map((i) => (
            <div key={i} className="flex items-center p-4">
              <Skeleton className="w-8 h-8 rounded-full mr-4 bg-slate-700" />
              <div className="flex-1 space-y-2">
                <Skeleton className="h-3 w-1/3 rounded bg-slate-700" />
                <Skeleton className="h-4 w-1/2 rounded bg-slate-700" />
              </div>
            </div>
          ))
        ) : !latest ? (
          // 데이터가 없을 때 (Empty State)
          <div className="flex-1 flex items-center justify-center bg-slate-800/30 rounded-xl border border-slate-700/50">
            <EmptyState
              icon={<AlertCircle size={40} />}
              title="지표 데이터 없음"
              description="현재 확인할 수 있는 경제 지표가 없습니다."
            />
          </div>
        ) : (
          // 데이터 로드 완료 시
            <>
              <IndicatorItem
                label="기준 금리"
                value={`${latest.fedFundsRate}%`}
                icon={<DollarSign size={16} />}
                color="text-slate-200"
              />
              <IndicatorItem
                label="10년 물 금리"
                value={`${latest.us10y}%`}
                icon={<Activity size={16} />}
                color={
                  latest.us10y >= 4.0 ? "text-orange-400" : "text-blue-400"
                }
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
            </>
        )}
      </div>
    </section>
  );
};

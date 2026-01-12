import { Skeleton } from "../../../../components/ui/Skeleton";

const EconomicChartSkeleton = () => {
  return (
    <div className="flex-1 rounded-xl border border-slate-700 bg-slate-800/50 p-5 shadow-sm backdrop-blur-sm min-h-[400px]">
      {/* 제목 스켈레톤 */}
      <div className="mb-6">
        <Skeleton className="h-7 w-1/3 rounded-lg" />
      </div>

      {/* 차트 영역 스켈레톤 */}
      <div className="relative h-[500px] w-full">
        <Skeleton className="h-full w-full rounded-xl" />
      </div>

      {/* 하단 설명 텍스트 스켈레톤 */}
      <div className="mt-2 flex justify-end">
        <Skeleton className="h-4 w-1/4 rounded" />
      </div>
    </div>
  );
};

export default EconomicChartSkeleton;
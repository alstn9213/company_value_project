import { Skeleton } from "../../../../components/ui/Skeleton";

export const FinancialSummarySkeleton = () => {
  return (
    <div className="bg-card border border-slate-700/50 rounded-xl p-6 shadow-lg backdrop-blur-sm h-full">
      {/* 헤더 스켈레톤 */}
      <div className="mb-6 flex items-center gap-2">
        <Skeleton className="h-6 w-6 rounded-full" /> {/* 아이콘 자리 */}
        <Skeleton className="h-6 w-48" /> {/* 제목 텍스트 자리 */}
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-x-8 gap-y-8">
        {[1, 2, 3].map((index) => (
          <div key={index} className="flex flex-col gap-4">
            {/* 각 섹션 제목 (예: 손익계산서) */}
            <Skeleton className="h-5 w-24 mb-2" />
            
            <div className="space-y-3">
              {[1, 2, 3, 4, 5].map((row) => (
                <div key={row} className="flex justify-between items-center">
                  <Skeleton className="h-4 w-20" /> {/* 항목명 */}
                  <Skeleton className="h-4 w-16" /> {/* 값 */}
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
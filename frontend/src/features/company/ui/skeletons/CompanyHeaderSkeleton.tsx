import { Skeleton } from "../../../../components/ui/Skeleton";

export const CompanyHeaderSkeleton = () => {
  return (
    <div className="bg-card border border-slate-700/50 rounded-2xl p-8 shadow-lg backdrop-blur-sm flex flex-col md:flex-row justify-between items-center gap-6 w-full">
      {/* 좌측: 기업 프로필 스켈레톤 */}
      <div className="flex flex-col gap-2 w-full md:w-auto">
        {/* 티커 (예: AAPL) */}
        <Skeleton className="h-9 w-24 mb-1" />
        
        {/* 회사명 (예: Apple Inc.) */}
        <div className="flex items-center gap-3">
            <Skeleton className="h-7 w-48 md:w-64" />
        </div>

        {/* 거래소 및 섹터 태그 */}
        <div className="flex gap-2 mt-2">
          <Skeleton className="h-5 w-16 rounded-md" />
          <Skeleton className="h-5 w-20 rounded-md" />
        </div>
      </div>

      {/* 우측: 액션 버튼 및 등급 뱃지 스켈레톤 */}
      <div className="flex items-center gap-6">
        {/* 관심목록 버튼 (원형) */}
        <Skeleton className="h-10 w-10 rounded-full" />
        
        {/* 등급 뱃지 (둥근 사각형) */}
        <Skeleton className="h-12 w-32 rounded-xl" />
      </div>
    </div>
  );
};
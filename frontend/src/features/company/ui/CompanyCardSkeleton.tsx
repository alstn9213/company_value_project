import Skeleton from "../../../components/ui/Skeleton";

const CompanyCardSkeleton = () => {
  return (
    <div className="bg-card border border-slate-700/50 rounded-xl p-5">
      <div className="flex justify-between items-start mb-3">
        <div className="flex gap-2 items-start">
          {/* 등급 박스 */}
          <Skeleton className="w-10 h-10 rounded-lg" />
          <div className="space-y-1">
            {/* 티커 */}
            <Skeleton className="w-16 h-5 rounded" />
            {/* 거래소 뱃지 */}
            <Skeleton className="w-10 h-3 rounded" />
          </div>
        </div>
        {/* 점수 */}
        <Skeleton className="w-8 h-6 rounded" />
      </div>

      <div className="space-y-2 mt-4">
        {/* 회사명 */}
        <Skeleton className="w-3/4 h-4 rounded" />
        {/* 섹터 */}
        <Skeleton className="w-1/2 h-3 rounded" />
      </div>
    </div>
  );
};

export default CompanyCardSkeleton;
import { Skeleton } from "../../../components/ui/Skeleton";

export const ScoreAnalysisSkeleton = () => {
  return (
    <div className="h-full flex flex-col gap-4">
      {/* 1. 페널티/위험 경고 메시지 영역 (공간 확보) */}
      <Skeleton className="h-12 w-full rounded-lg bg-red-900/20" />

      {/* 2. 메인 분석 카드 */}
      <div className="bg-card border border-slate-700/50 rounded-xl p-6 flex-1 flex flex-col shadow-lg backdrop-blur-sm">
        
        {/* 헤더 (아이콘 + 제목) */}
        <div className="mb-6 flex items-center gap-2">
            <Skeleton className="h-6 w-6 rounded-full" />
            <Skeleton className="h-6 w-32" />
        </div>

        {/* 종합 점수 표시 (큰 텍스트) */}
        <div className="flex flex-col items-center justify-center gap-2 mb-8">
            <Skeleton className="h-10 w-24" /> {/* 점수 */}
            <Skeleton className="h-4 w-32" />  {/* 설명 */}
        </div>

        {/* 레이더 차트 영역 (원형으로 흉내) */}
        <div className="flex-1 min-h-[200px] flex items-center justify-center mb-6">
          <Skeleton className="h-48 w-48 rounded-full opacity-20" />
        </div>

        {/* 세부 점수 리스트 (여러 줄) */}
        <div className="space-y-4 mt-auto">
            {[1, 2, 3, 4, 5].map((i) => (
                <div key={i} className="flex justify-between items-center">
                    <Skeleton className="h-4 w-20" /> {/* 항목명 */}
                    <Skeleton className="h-4 w-12" /> {/* 점수 */}
                </div>
            ))}
        </div>
      </div>
    </div>
  );
};
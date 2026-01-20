import { EmptyState } from "../../../components/ui/EmptyState";
import { ErrorState } from "../../../components/ui/ErrorState";
import { useInversionIntervals } from "../hooks/useInversionIntervals";
import { useMacroHistory } from "../hooks/useMacroQueries";
import { EconomicChartSkeleton } from "../ui/skeleton/EconomicChartSkeleton";
import { ChartCard } from "../../../components/ui/ChartCard";
import { EconomicLineChart } from "../ui/EconomicLineChart";
import { Globe2 } from "lucide-react";

export const EconomicChart = () => {
  const { history, error, isLoading } = useMacroHistory();
  const inversionIntervals = useInversionIntervals(history);

  if (isLoading) {
    return <EconomicChartSkeleton />;
  }

  if (error) {
    return (
      <ChartCard centerContent>
        <ErrorState
          title="데이터를 불러올 수 없습니다"
          message="잠시 후 다시 시도해주세요."
          onRetry={() => window.location.reload()}
        />
      </ChartCard>
    );
  }

  if (!history || history.length === 0) {
    return (
      <ChartCard centerContent>
        <EmptyState
          title="데이터가 없습니다"
          description="현재 표시할 경제 지표 데이터가 없습니다."
        />
      </ChartCard>
    );
  }

  return (
    <ChartCard className="h-auto min-h-[400px] p-5 bg-slate-800/50 backdrop-blur-sm">
      {/* 제목 영역 */}
      <div className="flex flex-col md:flex-row md:items-center justify-between mb-6 gap-4">
        <div>
          <h3 className="text-lg font-bold text-slate-200 flex items-center gap-2">
            <Globe2 className="w-5 h-5 text-blue-400" />
            미국 주요 금리 및 인플레이션
          </h3>
            <p className="text-xs text-slate-500 mt-1">
              최근 10년간의 국채 금리와 소비자 물가 지수 추이
            </p>
        </div>
      </div>
      {/* 차트 영역 */}
      <EconomicLineChart 
        data={history} 
        inversionIntervals={inversionIntervals} 
      />
    </ChartCard>
  );
};

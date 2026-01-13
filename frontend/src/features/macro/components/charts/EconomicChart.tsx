import { EmptyState } from "../../../../components/common/EmptyState";
import { ErrorState } from "../../../../components/common/ErrorState";
import { MacroData } from "../../../../types/macro";
import { useInversionIntervals } from "../../hooks/useInversionIntervals";
import { EconomicChartSkeleton } from "./EconomicChartSkeleton";
import { EconomicLineChart } from "./EconomicLineChart";

interface EconomicChartProps {
  history?: MacroData[];
  error?: boolean;
  isLoading: boolean;
}

export const EconomicChart = ({ history, error, isLoading }: EconomicChartProps) => {
  const inversionIntervals = useInversionIntervals(history);

  if (isLoading) {
    return <EconomicChartSkeleton />;
  }

  if (error) {
    return (
      <ErrorState
        title="데이터를 불러올 수 없습니다"
        message="잠시 후 다시 시도해주세요."
        onRetry={() => window.location.reload()}
      />
    );
  }

  // 데이터가 없거나 지표 목록이 비어있는 경우 처리
  if (!history || history.length === 0) {
    return (
      <EmptyState
        title="데이터가 없습니다"
        description="현재 표시할 경제 지표 데이터가 없습니다."
      />
    );
  }

  return (
  <div className="min-h-[400px] flex-1 rounded-xl border border-slate-700 bg-slate-800/50 p-5 shadow-sm backdrop-blur-sm">
    <h3 className="mb-6 text-lg font-bold text-slate-200">
      미국의 주요 금리 및 인플레이션 추이 (최근 10년)
    </h3>

    <EconomicLineChart data={history} inversionIntervals={inversionIntervals} />

    <div className="mt-2 text-right text-xs text-slate-500">
      * 붉은색 영역: 장단기 금리차 역전 구간
    </div>
  </div>
);
};

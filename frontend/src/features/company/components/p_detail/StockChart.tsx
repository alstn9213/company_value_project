import { BarChart2 } from "lucide-react";
import { StockPriceChart } from "../../../valuation/layouts/charts/StockPriceChart";
import { ErrorState } from "../../../../components/common/ErrorState";
import { EmptyState } from "../../../../components/ui/EmptyState";
import { LoadingState } from "../../../../components/ui/LoadingState";
import { useStockHistory } from "../../hooks/useStockHistory";

interface StockChartSectionProps {
  ticker: string;
}

export const StockChart = ({ticker}: StockChartSectionProps) => {
  const { 
    data: StockHistoryResponse, 
    isPending, 
    isError, 
    refetch 
  } = useStockHistory(ticker);

  // 공통 컨테이너 스타일: 높이 고정 및 중앙 정렬 (Layout Shift 방지)
  const containerClass = "w-full h-[350px] bg-slate-800/30 rounded-xl flex items-center justify-center border border-slate-700/50";

  if (isPending) {
    return (
      <div className={containerClass}>
        <LoadingState message="주가 데이터를 분석하고 있습니다..." />
      </div>
    );
  }

  if (isError) {
    return (
      <div className={containerClass}>
        <ErrorState 
          title="차트 로딩 실패" 
          onRetry={refetch} // React Query의 refetch 함수 연결
        />
      </div>
    );
  }

  if (!StockHistoryResponse || StockHistoryResponse.length === 0) {
    return (
      <div className={containerClass}>
        <EmptyState 
          icon={<BarChart2 className="w-10 h-10 text-slate-600" />} // 차트 아이콘
          title="주가 데이터 없음"
          description="해당 종목의 차트 정보를 불러올 수 없습니다."
        />
      </div>
    );
  }

  return (
    <div className="animate-in fade-in slide-in-from-bottom-4 duration-500 mb-4">
      <StockPriceChart data={StockHistoryResponse} />
    </div>
  );
};

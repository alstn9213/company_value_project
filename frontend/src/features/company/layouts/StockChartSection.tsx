import { BarChart2 } from "lucide-react";
import { StockPriceChart } from "../components/p_detail/StockPriceChart";
import { ErrorState } from "../../../components/common/ErrorState";
import { EmptyState } from "../../../components/ui/EmptyState";
import { LoadingState } from "../../../components/ui/LoadingState";
import { useStockHistory } from "../hooks/useStockHistory";
import { StockChartHeader } from "../components/p_detail/StockChartHeader";

interface StockChartSectionProps {
  ticker: string;
}

export const StockChartSection = ({ ticker }: StockChartSectionProps) => {
  const { 
    data: stockHistory, 
    isPending, 
    isError, 
    refetch 
  } = useStockHistory(ticker);

  // 컨테이너 스타일
  const containerClass = "w-full h-[350px] bg-slate-800/30 rounded-xl border border-slate-700/50 flex flex-col";
  // 로딩/에러용 중앙 정렬 스타일
  const centerClass = "w-full h-[350px] bg-slate-800/30 rounded-xl border border-slate-700/50 flex items-center justify-center";

  if (isPending) {
    return (
      <div className={centerClass}>
        <LoadingState message="주가 데이터를 분석하고 있습니다..." />
      </div>
    );
  }

  if (isError) {
    return (
      <div className={centerClass}>
        <ErrorState 
          title="차트 로딩 실패" 
          onRetry={refetch} // React Query의 refetch 함수 연결
        />
      </div>
    );
  }

  if (!stockHistory || stockHistory.length === 0) {
    return (
      <div className={centerClass}>
        <EmptyState 
          icon={<BarChart2 className="w-10 h-10 text-slate-600" />}
          title="주가 데이터 없음"
          description="해당 종목의 차트 정보를 불러올 수 없습니다."
        />
      </div>
    );
  }

  const sortedData = [...stockHistory].sort((a, b) => (a.date > b.date ? 1 : -1));
  const latestPrice = sortedData[sortedData.length - 1].close;

  return (
    <div className={`animate-in fade-in slide-in-from-bottom-4 duration-500 mb-4 ${containerClass} p-4`}>
      <StockChartHeader latestPrice={latestPrice} />
      <div className="animate-in fade-in slide-in-from-bottom-4 duration-500 mb-4">
        <StockPriceChart data={sortedData} />
      </div>
    </div>
  );
};

import { BarChart2 } from "lucide-react";
import { StockPriceChart } from "../components/p_detail/StockPriceChart";
import { ErrorState } from "../../../components/common/ErrorState";
import { EmptyState } from "../../../components/ui/EmptyState";
import { LoadingState } from "../../../components/ui/LoadingState";
import { useStockHistory } from "../hooks/useStockHistory";
import { StockChartHeader } from "../components/p_detail/StockChartHeader";
import { useMemo } from "react";
import { ChartCard } from "../../../components/ui/ChartCard";


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

  const sortedData = useMemo(() => {
    if (!stockHistory) return [];
    return [...stockHistory].sort((a, b) => (a.date > b.date ? 1 : -1));
  }, [stockHistory]);

  const latestPrice = sortedData.length > 0 ? sortedData[sortedData.length - 1].close : 0; 

  if (isPending) {
    return (
      <ChartCard centerContent>
        <LoadingState message="주가 데이터를 분석하고 있습니다..." />
      </ChartCard>
    );
  }

  if (isError) {
    return (
      <ChartCard centerContent>
        <ErrorState title="차트 로딩 실패" onRetry={refetch} />
      </ChartCard>
    );
  }

  if (!stockHistory || stockHistory.length === 0) {
    return (
      <ChartCard centerContent>
        <EmptyState 
          icon={<BarChart2 className="w-10 h-10 text-slate-600" />}
          title="주가 데이터 없음"
          description="데이터를 불러올 수 없습니다."
        />
      </ChartCard>
    );
  }

  return (
    <ChartCard>
      <StockChartHeader latestPrice={latestPrice} />
      <div className="flex-1 w-full min-h-0 mt-4">
        <StockPriceChart data={sortedData} />
      </div>
    </ChartCard>
  );
};

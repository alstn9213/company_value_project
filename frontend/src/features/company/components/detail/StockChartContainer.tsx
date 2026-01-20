import { BarChart2 } from "lucide-react";
import { ErrorState } from "../../../../components/ui/ErrorState";
import { EmptyState } from "../../../../components/ui/EmptyState";
import { LoadingState } from "../../../../components/ui/LoadingState";
import { StockChartHeader } from "./StockChartHeader";
import { ChartCard } from "../../../../components/ui/ChartCard";
import { useStockHistory } from "../../hooks/useStockHistory";
import { StockHistoryChart } from "../../ui/p_detail/StockHistoryChart";


interface StockChartProps {
  ticker: string;
}

export const StockChartContainer = ({ ticker }: StockChartProps) => {
  const { 
    stockHistory, 
    latestPrice, 
    isEmpty,
    isLoading, 
    isError, 
  } = useStockHistory(ticker);

  if (isLoading) {
    return (
      <ChartCard centerContent>
        <LoadingState message="주가 데이터를 분석하고 있습니다..." />
      </ChartCard>
    );
  }

  if (isError) {
    return (
      <ChartCard centerContent>
        <ErrorState title="차트 로딩 실패"/>
      </ChartCard>
    );
  }

  if (isEmpty) {
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
        <StockHistoryChart data={stockHistory} />
      </div>
    </ChartCard>
  );
};

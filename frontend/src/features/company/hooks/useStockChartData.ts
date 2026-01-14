import { useMemo } from "react";
import { useStockHistory } from "./useStockHistory";

export const useStockChartData = (ticker: string) => {
  const { data: stockHistory, ...rest } = useStockHistory(ticker);

  const { sortedData, latestPrice } = useMemo(() => {
    if (!stockHistory || stockHistory.length === 0) {
      return { sortedData: [], latestPrice: 0 };
    }

    // 원본 불변성 유지를 위해 복사 후 정렬
    const sorted = [...stockHistory].sort((a, b) => 
      new Date(a.date).getTime() - new Date(b.date).getTime()
    );

    const price = sorted.length > 0 ? sorted[sorted.length - 1].close : 0;

    return { sortedData: sorted, latestPrice: price };
  }, [stockHistory]);

  return {
    stockHistory: sortedData, // 정렬된 데이터를 메인 데이터로 반환
    latestPrice,
    isEmpty: !stockHistory || stockHistory.length === 0, // 빈 상태 체크 로직도 훅에서 처리
    ...rest,
  };
};
import { useQuery } from "@tanstack/react-query";
import { companyApi } from "../../../api/companyApi";
import StockPriceChart from "../../../components/charts/StockPriceChart";

interface Props {
  ticker: string;
}

const StockChartSection = ({ticker}: Props) => {
  
  const {data: stockHistory, isPending, isError} = useQuery({
    queryKey: ["companyStock", ticker],
    queryFn: async () => companyApi.getStockHistory(ticker),
    enabled: !!ticker,
    staleTime: 1000 * 60 * 60, // 1시간 캐싱
  });

  if(isPending) {
    return (
      <div className="w-full h-[350px] bg-slate-800/30 rounded-xl flex items-center justify-center text-slate-400 border border-slate-700/50 animate-pulse">
        주가 데이터 로딩 중...
      </div>
    );
  }

  // 에러가 발생했거나 데이터가 비어있는 경우 처리
  if(isError || !stockHistory || stockHistory.length === 0) {
    return (
      <div className="w-full h-[350px] bg-slate-800/30 rounded-xl flex flex-col items-center justify-center text-slate-500 border border-slate-700/50">
        <p>📉 주가 데이터를 불러올 수 없습니다.</p>
        <span className="text-xs mt-2">일시적인 오류이거나 데이터가 없습니다.</span>
      </div>
    );
  }

  return (
    <div className="animate-in fade-in slide-in-from-bottom-4 duration-500 mb-4">
      <StockPriceChart data={stockHistory} />
    </div>
  );
};

export default StockChartSection;
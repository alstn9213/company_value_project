import { formatCurrency } from "../../../../utils/formatters";

interface StockChartHeaderProps {
  latestPrice: number;
}

export const StockChartHeader = ({ latestPrice }: StockChartHeaderProps) => {

  if (!latestPrice) {
    return null;
  }

  return (
    <div className="flex justify-between items-start mb-4 pl-2 shrink-0">
      <h3 className="text-lg font-bold text-slate-200 border-l-4 border-emerald-500 pl-2">
        📈 주가 변동 (1년)
      </h3>
      <div className="text-right">
        <span>
        현재가 :
        </span>
        <span className="text-xl font-bold text-white">
          {formatCurrency(latestPrice)}
        </span>
        
      </div>
    </div>
  );
};
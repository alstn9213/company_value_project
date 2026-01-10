import { useQuery } from "@tanstack/react-query";
import { StockHistory } from "../../../types/company";
import { companyApi } from "../../../api/companyApi";

export const COMPANY_KEYS = {
  all: ["company"] as const,
  stockHistory: (ticker: string) => [...COMPANY_KEYS.all, "stock", ticker] as const,
};

export const useStockHistory = (ticker: string) => {
  return useQuery<StockHistory[]>({
    queryKey: COMPANY_KEYS.stockHistory(ticker),
    queryFn: () => companyApi.getStockHistory(ticker),
    enabled: !!ticker,
    staleTime: 1000 * 60 * 60, // 1시간 캐싱
    // 필요하다면 select 옵션을 통해 데이터 가공 로직도 여기에 넣을 수 있다.
  });
};
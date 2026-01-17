import { useQuery } from "@tanstack/react-query";
import { StockHistoryResponse } from "../../../types/company";
import { companyApi } from "../api/companyApi";
import { getErrorMessage } from "../../../utils/errorHandler";
import { useMemo } from "react";
import { ApiErrorData } from "../../../types/auth";
import { AxiosError } from "axios";

export const STOCK_KEYS = {
  all: ["stock"] as const,
  history: (ticker: string) => [...STOCK_KEYS.all, "history", ticker] as const,
};

export interface StockHistoryHookResult {
  stockHistory: StockHistoryResponse[];
  latestPrice: number;
  isLoading: boolean;
  isError: boolean;
  errorMessage: string | undefined;
  isEmpty: boolean;
}

export const useStockHistory = (ticker: string): StockHistoryHookResult => {
  const { 
    data: sortedHistory, // select를 거쳐 정렬된 데이터가 들어옴
    isLoading, 
    isError, 
    error 
  } = useQuery<StockHistoryResponse[], AxiosError<ApiErrorData>, StockHistoryResponse[]>({
    queryKey: STOCK_KEYS.history(ticker),
    queryFn: () => companyApi.getStockHistoryResponse(ticker),
    enabled: !!ticker,
    staleTime: 1000 * 60 * 60, // 1시간 캐싱
    
    // [최적화 포인트] select 옵션을 사용해 데이터 변환 로직 내재화
    // 서버에서 받은 데이터를 날짜순으로 정렬합니다. 
    // 데이터가 변하지 않으면 이 함수는 재실행되지 않습니다 (Memoization).
    select: (data) => {
      return [...data].sort((a, b) => 
        new Date(a.date).getTime() - new Date(b.date).getTime()
      );
    },
  });

  const errorMessage = getErrorMessage(error);

  // 파생 데이터 계산 (정렬된 데이터 기반)
  const { latestPrice, isEmpty } = useMemo(() => {
    if (!sortedHistory || sortedHistory.length === 0) {
      return { latestPrice: 0, isEmpty: true };
    }
    // 이미 정렬되어 있으므로 마지막 요소가 최신 가격
    const lastItem = sortedHistory[sortedHistory.length - 1];
    return { 
      latestPrice: lastItem.close, 
      isEmpty: false 
    };
  }, [sortedHistory]);

  return {
    stockHistory: sortedHistory || [], // undefined 방지
    latestPrice,
    isLoading,
    isError,
    errorMessage,
    isEmpty,
  };
};
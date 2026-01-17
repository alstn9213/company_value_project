import { useQuery } from "@tanstack/react-query";
import { macroApi } from "../api/macroApi";
import { AxiosError } from "axios";
import { MacroDataResponse } from "../../../types/macro";

const MACRO_KEYS = {
  all: ["macro"] as const,
  latest: () => [...MACRO_KEYS.all, "latest"] as const,
  history: () => [...MACRO_KEYS.all, "history"] as const,
};

export interface MacroLatestHookResult {
  macroData: MacroDataResponse | undefined;
  isLoading: boolean;
  isError: boolean;
  error: AxiosError | null;
  refetch: () => void;
}

export const useMacroLatest = (): MacroLatestHookResult => {
  const { data, isLoading, isError, error, refetch } = useQuery<MacroDataResponse, AxiosError>({
    queryKey: MACRO_KEYS.latest(),
    queryFn: macroApi.getLatest,
    staleTime: 1000 * 60 * 60, // 1시간 동안 데이터를 신선한 상태로 유지 (캐싱)
    retry: 1, // 실패 시 1회 재시도
  });

  return {
    macroData: data,
    isLoading,
    isError,
    error,
    refetch,
  };
};

export interface MacroHistoryHookResult {
  history: MacroDataResponse[];
  isLoading: boolean;
  isError: boolean;
  error: AxiosError | null;
  refetch: () => void;
}

export const useMacroHistory = (): MacroHistoryHookResult => {
  const { data, isLoading, isError, error, refetch } = useQuery<MacroDataResponse[], AxiosError>({
    queryKey: MACRO_KEYS.history(),
    queryFn: macroApi.getHistory,
    staleTime: 1000 * 60 * 60 * 24, // 24시간 (변동이 적은 과거 데이터)
  });

  return {
    history: data || [], // 데이터가 없을 경우 빈 배열 반환하여 안전성 확보
    isLoading,
    isError,
    error,
    refetch,
  };
};
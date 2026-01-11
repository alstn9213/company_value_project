import { useQuery } from "@tanstack/react-query";
import { macroApi } from "../api/macroApi";

export const useMacroLatest = () => {
  return useQuery({
    queryKey: ["macroLatest"],
    queryFn: macroApi.getLatest,
    staleTime: 1000 * 60 * 60, // 1시간 캐싱
  });
};

export const useMacroHistory = () => {
  return useQuery({
    queryKey: ["macroHistory"],
    queryFn: macroApi.getHistory,
    staleTime: 1000 * 60 * 60 * 24, // 과거 데이터는 잘 안 변하므로 긴 시간 캐싱
  });
};
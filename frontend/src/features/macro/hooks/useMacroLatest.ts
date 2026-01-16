import { useQuery } from "@tanstack/react-query";
import { macroApi } from "../api/macroApi";

export const useMacroLatest = () => {
  return useQuery({
    queryKey: ["macroLatest"],
    queryFn: macroApi.getLatest,
    staleTime: 1000 * 60 * 60, // 1시간 캐싱
  });
};

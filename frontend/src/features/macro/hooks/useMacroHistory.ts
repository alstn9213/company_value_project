import { useQuery } from "@tanstack/react-query";
import { macroApi } from "../api/macroApi";
import { AxiosError } from "axios";
import { ApiErrorData } from "../../../types/auth";
import { MacroDataResponse } from "../../../types/macro";

export const useMacroHistory = () => {
  return useQuery<MacroDataResponse[], AxiosError<ApiErrorData>>({
    queryKey: ["macroHistory"],
    queryFn: macroApi.getHistory,
    staleTime: 1000 * 60 * 60 * 24, // 과거 데이터는 잘 안 변하므로 긴 시간 캐싱
  });
};

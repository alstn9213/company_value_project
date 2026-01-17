import { AxiosError } from "axios";
import { ApiErrorData } from "../types/auth";

export const getErrorMessage = (error: AxiosError<ApiErrorData> | null): string | undefined => {
  if (!error) {
    return undefined;
  }

  const responseData = error.response?.data;

  if (typeof responseData === "string") {
    return responseData;
  }
  
  if (responseData && typeof responseData === 'object' && 'message' in responseData) {
      return responseData.message;
  }

  return "데이터를 불러오는 중 오류가 발생했습니다.";
};
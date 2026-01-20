import { isAxiosError } from "axios";
import { ApiErrorResponse } from "../types/api";

export const getErrorMessage = (error: unknown): string => {
  // Axios 에러인지 확인 (서버 요청 관련 에러)
  if (isAxiosError<ApiErrorResponse>(error)) {
    // 1-1. 서버로부터 응답이 온 경우 (4xx, 5xx)
    if (error.response && error.response.data) {
      // 이제 data는 무조건 ApiErrorResponse 객체이므로 .message 접근 가능
      return error.response.data.message;
    }

    // 1-2. 요청은 갔으나 응답이 없는 경우 (네트워크 에러, 타임아웃 등)
    if (error.request) {
      return "서버와 통신할 수 없습니다. 네트워크 연결을 확인해주세요.";
    }

    // 1-3. 요청 설정 중 에러 발생
    return error.message;
  }

  // 2. 일반 JS 에러인 경우
  if (error instanceof Error) {
    return error.message;
  }

  // 3. 그 외 알 수 없는 에러
  return "알 수 없는 오류가 발생했습니다.";
};
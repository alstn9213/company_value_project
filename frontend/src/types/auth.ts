export interface User {
  nickname: string;
  email: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface SignUpRequest {
  email: string;
  password:string;
  nickname: string;
}

export interface TokenResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  nickname: string;
}

/**
 * API 에러 응답을 위한 공통 타입.
 * 에러 데이터는 message 속성을 가진 객체이거나, 단순 문자열일 수 있다.
 */
export type ApiErrorData = { message: string } | string;
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


export interface ApiErrorResponse {
  code: string;    // 에러 식별 코드
  message: string; // 사용자에게 보여줄 에러 메시지
  status?: number; // JwtAuthenticationEntryPoint에서는 status를 포함하므로 optional 처리
  timestamp?: string; // ErrorResponse 객체에서는 timestamp를 포함하므로 optional 처리
}

export type ApiErrorData = ApiErrorResponse;
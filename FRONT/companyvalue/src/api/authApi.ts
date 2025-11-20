import { LoginRequest, SignUpRequest, TokenResponse } from "../types/auth";
import axiosClient from "./axiosClient";

export const authApi = {
  
  login: async (data: LoginRequest): Promise<TokenResponse> => {
    const response = await axiosClient.post<TokenResponse>('/auth/login', data);
    return response.data;
  },

  signup: async (data: SignUpRequest): Promise<string> => {
    const response = await axiosClient.post<string>('/auth/signup', data);
    return response.data; // '회원가입 성공' 메시지
  }
}
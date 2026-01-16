import axiosClient from "../../../api/axiosClient";
import { LoginRequest, SignUpRequest, TokenResponse } from "../../../types/auth";

export const authApi = {
  
  login: async (data: LoginRequest): Promise<TokenResponse> => {
    const response = await axiosClient.post<TokenResponse>('/auth/login', data);
    return response.data;
  },

  signup: async (data: SignUpRequest): Promise<string> => {
    const response = await axiosClient.post<string>('/api/members/signup', data);
    return response.data; // '회원가입 성공' 메시지
  }
}
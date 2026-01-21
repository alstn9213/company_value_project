import { create } from "zustand";
import { User } from "../types/auth";

interface AuthState {
  token: string | null;
  user: User | null;
  isAuthenticated: boolean;
  login: (token: string, nickname: string, email: string) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>((set) => {
  const token = localStorage.getItem("accessToken");
  const nickname = localStorage.getItem("nickname");
  const email = localStorage.getItem("email");

  return {
    // 초기화 시 로컬 스토리지 확인
    token,
    user: nickname ? { nickname, email: email || "" } : null,
    isAuthenticated: !!token,

    login: (token, nickname, email) => {
    localStorage.setItem("accessToken", token);
    localStorage.setItem("nickname", nickname);
    localStorage.setItem("email", email);
    set({
      token,
      user: { nickname, email },
      isAuthenticated: true
      });
    },

    logout: () => {
      localStorage.removeItem("accessToken");
      localStorage.removeItem("nickname");
      localStorage.removeItem("email");
      set({
        token: null,
        user: null,
        isAuthenticated: false
      });
    },
  
  };
});

import { create } from "zustand";
import { User } from "../types/auth";

interface AuthState {
  token: string | null;
  user: User | null;
  isAuthenticated: boolean;
  login: (token: string, nickname: string, email: string) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  // 초기화 시 로컬 스토리지 확인
  token: localStorage.getItem("accessToken"),
  user: localStorage.getItem("nickname")
    ? { nickname: localStorage.getItem("nickname")!, email: "" } // 이메일은 토큰 디코딩 필요하나 여기선 닉네임 위주로 저장
    : null,
  isAuthenticated: !!localStorage.getItem("accessToken"),

  login: (token, nickname, email) => {
    localStorage.setItem("accessToken", token);
    localStorage.setItem("nickname", nickname);
    set({
      token,
      user: { nickname, email },
      isAuthenticated: true
    });
  },

  logout: () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("nickname");
    set({
      token: null,
      user: null,
      isAuthenticated: false
    });
  },
}));

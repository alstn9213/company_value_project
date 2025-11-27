import axios from 'axios';
import { useAuthStore } from '../stores/authStore';

const axiosClient = axios.create({
  baseURL: 'http://localhost:8080',
  headers: {
    'Content-Type' : 'application/json',
  },
});

// 모든 요청 헤더에 토큰 주입
axiosClient.interceptors.request.use(
  (config) => {
    const token = useAuthStore.getState().token;
    if(token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 401 에러 시 자동 로그아웃 처리
axiosClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if(error.response && error.response.status === 401) {
      useAuthStore.getState().logout(); // 토큰 만료 또는 인증 실패 시 스토어 비우기 및 이동
      window.location.href = '/login'; // 로그인 페이지로 강제 이동 (강제 리다이렉트)
    }
    return Promise.reject(error);
  }
);

export default axiosClient;
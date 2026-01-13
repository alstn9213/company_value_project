import { Navigate, Outlet } from 'react-router-dom';
import { useAuthStore } from "../../stores/authStore"

export const ProtectedRoute = () => {
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);

  if(!isAuthenticated) {
    // 로그인 상태가 아니면 로그인 페이지로 리다이렉트
    return <Navigate to="/login" replace />
  }

  // 로그인 상태면 하위 라우트(Outlet) 렌더링
  return <Outlet />;
};

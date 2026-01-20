import { Navigate, Outlet } from 'react-router-dom';
import { useAuthStore } from "../../stores/authStore"

export const ProtectedRoute = () => {
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);

  if(!isAuthenticated) {
    return <Navigate to="/login" />
  }

  // 로그인 상태면 하위 라우트(Outlet) 렌더링
  return <Outlet />;
};

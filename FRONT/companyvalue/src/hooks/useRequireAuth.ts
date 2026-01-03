import { useNavigate } from "react-router-dom";
import { useAuthStore } from "../stores/authStore";
import { useCallback } from "react";

export const useRequireAuth = () => {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuthStore(); //

  /**
   * 인증이 필요한 액션을 감싸서 실행하는 고차 함수(HOC) 스타일의 메서드입니다.
   * @param callback - 로그인이 되어 있을 때 실행할 함수
   */
  const withAuth = useCallback((callback: () => void) => {
    if (!isAuthenticated) {
      if (confirm("로그인이 필요한 기능입니다. 로그인 페이지로 이동하시겠습니까?")) {
        // 추후 UX 개선: 로그인 후 다시 현재 페이지로 돌아오도록 state에 현재 경로(location.pathname)를 넘길 수도 있습니다.
        navigate("/login");
      }
      return; // 로그인 페이지로 이동하거나 취소했으므로 원래 액션은 실행하지 않음
    }

    // 로그인 상태라면 콜백 실행
    callback();
  }, [isAuthenticated, navigate]);

  return { withAuth, isAuthenticated };
};
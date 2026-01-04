import { Link, useNavigate } from "react-router-dom";
import { LogIn, LogOut } from "lucide-react";
import { useAuthStore } from "../../stores/authStore";

const UserMenu = () => {
  const navigate = useNavigate();
  const { user, logout, isAuthenticated } = useAuthStore();

  const handleLogout = () => {
    if (confirm("로그아웃 하시겠습니까?")) {
      logout();
      navigate("/");
    }
  };

  // 1. 로그인 상태일 때 UI
  if (isAuthenticated) {
    return (
      <div className="flex items-center gap-4">
        {/* 모바일에서는 숨기고, 태블릿 이상에서만 사용자 정보 표시 */}
        <div className="hidden text-right sm:block">
          <p className="text-xs text-slate-500">Welcome,</p>
          <p className="text-sm font-bold text-emerald-400">
            {user?.nickname || "User"}
          </p>
        </div>
        
        <div className="h-8 w-[1px] bg-slate-800 hidden sm:block"></div>
        
        <button
          onClick={handleLogout}
          className="flex items-center gap-2 rounded-lg px-3 py-2 text-sm font-medium text-slate-400 transition-all hover:bg-red-500/10 hover:text-red-400"
        >
          <LogOut size={18} />
          <span className="hidden sm:inline">로그아웃</span>
        </button>
      </div>
    );
  }

  // 2. 비로그인 상태일 때 UI
  return (
    <div className="flex flex-row items-center gap-3">
      <Link
        to="/login"
        className="flex items-center gap-2 rounded-lg px-4 py-2 text-sm font-medium text-slate-300 hover:bg-slate-800 hover:text-white transition-colors"
      >
        <LogIn size={18} />
        <span>로그인</span>
      </Link>
      <Link
        to="/signup"
        className="whitespace-nowrap rounded-lg bg-blue-600 px-4 py-2 text-sm font-bold text-white shadow-lg shadow-blue-900/20 transition-all hover:bg-blue-500"
      >
        회원가입
      </Link>
    </div>
  );
};

export default UserMenu;
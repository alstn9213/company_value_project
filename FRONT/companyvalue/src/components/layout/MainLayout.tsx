import {
  LayoutDashboard,
  LogIn,
  LogOut,
  Search,
  Star,
  TrendingUp,
  UserCircle,
} from "lucide-react";
import { Link, Outlet, useLocation, useNavigate } from "react-router-dom";
import { useAuthStore } from "../../stores/authStore";

const MainLayout = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { user, logout, isAuthenticated } = useAuthStore();

  // 메뉴 아이템 정의
  const menus = [
    { name: "대시보드", path: "/", icon: <LayoutDashboard size={20} /> },
    { name: "기업 찾기", path: "/companies", icon: <Search size={20} /> },
  ];

  const handleLogout = () => {
    if (confirm("로그아웃 하시겠습니까?")) {
      logout();
      navigate("/login");
    }
  };

  return (
    <div className="flex min-h-screen bg-[#0f172a] text-slate-200 font-sans">
      {/* Sidebar (좌측 메뉴) */}
      <aside className="w-64 flex-shrink-0 border-r border-slate-800 bg-[#0f172a] flex flex-col fixed h-full z-10 transition-all duration-300">
        {/* Logo Area */}
        <div className="p-6 flex items-center gap-2">
          <div className="bg-emerald-500/20 p-2 rounded-lg text-emerald-400">
            <TrendingUp size={24} />
          </div>
          <h1 className="text-xl font-bold text-white tracking-wide">
            Value Pick
          </h1>
        </div>

        {/* User Info */}
        {isAuthenticated && (
          <div className="px-6 py-4 mb-2 border-b border-slate-800">
            <p className="text-xs text-slate-500 uppercase mb-1">Welcome</p>
            <p className="text-sm font-medium text-emerald-400 truncate">
              {user?.nickname || "User"} 님
            </p>
          </div>
        )}

        {/* Navigation Links */}
        <nav className="flex-1 px-4 space-y-2 py-4 overflow-y-auto">
          {menus.map((menu) => {
            const isActive = location.pathname === menu.path;
            return (
              <Link
                key={menu.path}
                to={menu.path}
                className={`flex items-center gap-3 px-4 py-3 rounded-xl transition-all duration-200 group ${
                  isActive
                    ? "bg-blue-600 text-white shadow-lg shadow-blue-900/20"
                    : "text-slate-400 hover:bg-slate-800 hover:text-slate-100"
                }`}
              >
                <span
                  className={
                    isActive
                      ? "text-white"
                      : "text-slate-400 group-hover:text-white"
                  }
                >
                  {menu.icon}
                </span>
                <span className="font-medium">{menu.name}</span>
              </Link>
            );
          })}
          {/* 관심 종목 메뉴는 로그인 했을 때만 표시 */}
          {isAuthenticated && (
            <Link
              to="/watchlist"
              className={`flex items-center gap-3 px-4 py-3 rounded-xl transition-all duration-200 group ${
                location.pathname === "/watchlist"
                  ? "bg-blue-600 text-white shadow-lg shadow-blue-900/20"
                  : "text-slate-400 hover:bg-slate-800 hover:text-slate-100"
              }`}
            >
              <span
                className={
                  location.pathname === "/watchlist"
                    ? "text-white"
                    : "text-slate-400 group-hover:text-white"
                }
              >
                <Star size={20} />
              </span>
              <span className="font-medium">관심 종목</span>
            </Link>
          )}
        </nav>
      </aside>

      {/* Main Content Area (우측 컨텐츠) */}
      <main className="flex-1 ml-64 bg-[#0f172a] min-h-screen relative flex flex-col">
        {/* Background Glow Effect */}
        <div className="absolute top-0 left-0 w-full h-[500px] bg-gradient-to-b from-blue-900/10 to-transparent pointer-events-none" />
        {/* Header */}
        <header className="sticky top-0 z-20 flex justify-end items-center px-8 py-4 backdrop-blur-md bg-[#0f172a]/80 border-b border-slate-800/50">
          <div className="flex items-center gap-6">
            {isAuthenticated ? (
              <>
                {/* 로그인 상태 UI */}
                <div className="flex items-center gap-3">
                  <div className="text-right hidden sm:block">
                    <p className="text-xs text-slate-500">Welcome back,</p>
                    <p className="text-sm font-bold text-emerald-400">
                      {user?.nickname || 'User'}
                    </p>
                  </div>
                  <div className="bg-slate-800 p-2 rounded-full text-slate-400">
                    <UserCircle size={24} />
                  </div>
                </div>
                <div className="h-8 w-[1px] bg-slate-700"></div>
                <button onClick={handleLogout} className="flex items-center gap-2 px-3 py-2 rounded-lg text-slate-400 hover:bg-red-500/10 hover:text-red-400 transition-all duration-200 text-sm font-medium">
                  <LogOut size={18} />
                  <span>로그아웃</span>
                </button>
              </>
            ) : (
              <>
                {/* 비로그인 상태 UI */}
                <Link to="/login" className="flex items-center gap-2 px-4 py-2 rounded-lg bg-blue-600 text-white hover:bg-blue-500 transition-all shadow-lg shadow-blue-600/20 text-sm font-medium">
                  <LogIn size={18} />
                  <span>로그인</span>
                </Link>
                <Link to="/signup" className="text-sm font-medium text-slate-400 hover:text-white transition-colors">
                  회원가입
                </Link>
              </>
            )}
          </div>
        </header>

        {/* Page Content */}
        <div className="relative z-0 p-8 flex-1">
          <Outlet />
        </div>
      </main>
    </div>
  );
};

export default MainLayout;

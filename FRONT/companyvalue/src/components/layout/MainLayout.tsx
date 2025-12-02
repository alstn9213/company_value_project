import React, { useState } from "react";
import { LogIn, LogOut, Search, Star, TrendingUp } from "lucide-react";
import { Link, Outlet, useLocation, useNavigate } from "react-router-dom";
import { useAuthStore } from "../../stores/authStore";

const MainLayout = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { user, logout, isAuthenticated } = useAuthStore();
  const [keyword, setKeyword] = useState("");

  const menus = [
    { name: "기업 목록", path: "/companies", icon: <Search size={18} /> },
  ];

  const handleLogout = () => {
    if (confirm("로그아웃 하시겠습니까?")) {
      logout();
      navigate("/login");
    }
  };

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    if (keyword.trim()) {
      navigate(`/companies?search=${keyword}`);
    }
  };

  return (
    <div className="flex min-h-screen w-full flex-col bg-[#0f172a] text-slate-200 font-sans">
      {/* 상단 통합 헤더 (Top Navigation Bar) */}
      <header className="sticky top-0 z-50 w-full border-b border-slate-800 bg-[#0f172a]/90 backdrop-blur-md">
        {/* max-w 제한 제거 후 px 조정 */}
        <div className="mx-auto flex h-16 w-full items-center justify-between px-6 lg:px-10">
          {/* [Left] 로고 및 메인 메뉴 */}
          <div className="flex items-center gap-8">
            {/* 로고 (대시보드로 이동) */}
            <Link to="/" className="flex items-center gap-2 group">
              <div className="rounded-lg bg-emerald-500/20 p-2 text-emerald-400 transition-colors group-hover:bg-emerald-500/30">
                <TrendingUp size={24} />
              </div>
              <span className="text-xl font-bold tracking-wide text-white">
                VALUE PICK
              </span>
            </Link>

            {/* 내비게이션 링크 (Desktop) */}
            <nav className="hidden md:flex items-center gap-1">
              {menus.map((menu) => {
                const isActive = location.pathname === menu.path;
                return (
                  <Link
                    key={menu.path}
                    to={menu.path}
                    className={`flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-medium transition-all duration-200 ${
                      isActive
                        ? "bg-slate-800 text-white shadow-sm"
                        : "text-slate-400 hover:bg-slate-800/50 hover:text-slate-200"
                    }`}
                  >
                    {menu.icon}
                    <span>{menu.name}</span>
                  </Link>
                );
              })}
              {/* 관심 종목 (로그인 시 노출) */}
              {isAuthenticated && (
                <Link
                  to="/watchlist"
                  className={`flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-medium transition-all duration-200 ${
                    location.pathname === "/watchlist"
                      ? "bg-slate-800 text-white shadow-sm"
                      : "text-slate-400 hover:bg-slate-800/50 hover:text-slate-200"
                  }`}
                >
                  <Star size={18} />
                  <span>관심 종목</span>
                </Link>
              )}
            </nav>
          </div>

          {/* [Center & Right] 검색창 및 유저 메뉴 */}
          <div className="flex items-center gap-4 lg:gap-8">
            {/* 검색창 */}
            <form
              onSubmit={handleSearch}
              className="relative w-full max-w-sm hidden sm:block"
            >
              <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3">
                <Search className="h-4 w-4 text-slate-500" />
              </div>
              <input
                type="text"
                placeholder="티커(AAPL) 또는 기업명 검색"
                className="block w-full rounded-lg border border-slate-700 bg-slate-900 py-2 pl-10 pr-3 text-sm text-slate-200 placeholder-slate-500 focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500 transition-all"
                value={keyword}
                onChange={(e) => setKeyword(e.target.value)}
              />
            </form>

            {/* 로그인 상태 UI */}
            <div className="flex items-center gap-4">
              {isAuthenticated ? (
                <>
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
                </>
              ) : (
                <div className="flex items-center gap-3">
                  <Link
                    to="/login"
                    className="flex items-center gap-2 rounded-lg px-4 py-2 text-sm font-medium text-slate-300 hover:bg-slate-800 hover:text-white transition-colors"
                  >
                    <LogIn size={18} />
                    <span>로그인</span>
                  </Link>
                  <Link
                    to="/signup"
                    className="rounded-lg bg-blue-600 px-4 py-2 text-sm font-bold text-white shadow-lg shadow-blue-900/20 transition-all hover:bg-blue-500"
                  >
                    회원가입
                  </Link>
                </div>
              )}
            </div>
          </div>
        </div>
      </header>

      {/* 메인 컨텐츠 영역 (Full Width) */}
      <main className="flex-1 w-full relative">
        {/* 상단 배경 그라데이션 효과 (Visual Depth) */}
        <div className="absolute top-0 left-0 w-full h-[300px] bg-gradient-to-b from-blue-900/5 to-transparent pointer-events-none" />

        {/* 전체 너비 사용 */}
        <div className="relative z-0 w-full p-6 lg:p-8">
          <Outlet />
        </div>
      </main>
    </div>
  );
};

export default MainLayout;

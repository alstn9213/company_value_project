import { LayoutDashboard, LogOut, Search, Star, TrendingUp } from "lucide-react";
import { Link, Outlet, useLocation, useNavigate } from "react-router-dom";
import { useAuthStore } from "../../stores/authStore";

const MainLayout = () => {

  const location = useLocation();
  const navigate = useNavigate();
  const { user, logout } = useAuthStore();

  // 메뉴 아이템 정의
  const menus = [
    { name: '대시보드', path: '/', icon: <LayoutDashboard size={20}/>},
    { name: '기업 찾기', path: '/companies', icon: <Search size={20}/>},
    { name: '관심 종목', path: '/watchlist', icon: <Star size={20}/>},
  ];

  const handleLogout = () => {
    if(confirm('로그아웃 하시겠습니까?')) {
      logout();
      navigate('/login');
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

        {/* User Info (Optional) */}
        <div className="px-6 py-4 mb-2 border-b border-slate-800">
          <p className="text-xs text-slate-500 uppercase mb-1">Welcome</p>
          <p className="text-sm font-medium text-emerald-400 truncate">
            {user?.nickname || 'User'} 님
          </p>
        </div>

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
                    ? 'bg-blue-600 text-white shadow-lg shadow-blue-900/20'
                    : 'text-slate-400 hover:bg-slate-800 hover:text-slate-100'
                }`}
              >
                <span className={isActive ? 'text-white' : 'text-slate-400 group-hover:text-white'}>
                  {menu.icon}
                </span>
                <span className="font-medium">{menu.name}</span>
              </Link>
            );
          })}
        </nav>

        {/* Logout Button (Bottom) */}
        <div className="p-4 border-t border-slate-800">
          <button
            onClick={handleLogout}
            className="w-full flex items-center justify-center gap-2 px-4 py-3 rounded-lg text-slate-400 hover:bg-red-500/10 hover:text-red-400 transition-colors"
          >
            <LogOut size={18} />
            <span className="text-sm font-medium">로그아웃</span>
          </button>
        </div>
      </aside>

      {/* Main Content Area (우측 컨텐츠) */}
      <main className="flex-1 ml-64 bg-[#0f172a] min-h-screen relative">
        {/* Background Glow Effect (Optional Aesthetics) */}
        <div className="absolute top-0 left-0 w-full h-[500px] bg-gradient-to-b from-blue-900/10 to-transparent pointer-events-none" />
        
        <div className="relative z-0 p-8">
          {/* 실제 페이지 컴포넌트가 렌더링되는 위치 */}
          <Outlet />
        </div>
      </main>
    </div>
  );
};

export default MainLayout;
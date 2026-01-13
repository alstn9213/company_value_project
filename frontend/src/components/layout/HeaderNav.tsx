import { Link, useLocation } from "react-router-dom";
import { Search, Star } from "lucide-react";
import { useAuthStore } from "../../stores/authStore";

export const HeaderNav = () => {
  const location = useLocation();
  const { isAuthenticated } = useAuthStore();

  const menus = [
    { name: "기업 목록", path: "/companies", icon: <Search size={18} /> },
  ];

  return (
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
      
      {/* 로그인한 유저에게만 보이는 메뉴 */}
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
  );
};

import { useLocation } from "react-router-dom";
import { Search, Star } from "lucide-react";
import { useAuthStore } from "../../stores/authStore";
import { NavItem } from "./NavItem";

export const HeaderNav = () => {
  const location = useLocation();
  const { isAuthenticated } = useAuthStore();

  const menus = [
    { name: "기업 목록", path: "/companies", icon: <Search size={18} /> },
  ];

  return (
    <nav className="hidden md:flex items-center gap-1">
      {menus.map((menu) => (
        <NavItem
          key={menu.path}
          path={menu.path}
          name={menu.name}
          icon={menu.icon}
          isActive={location.pathname === menu.path}
        />
      ))}
      
      {isAuthenticated && (
        <NavItem
          path="/watchlist"
          name="관심 종목"
          icon={<Star size={18} />}
          isActive={location.pathname === "/watchlist"}
        />
      )}
    </nav>
  );
};

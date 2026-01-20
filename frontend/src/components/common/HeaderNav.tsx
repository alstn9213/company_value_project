import { useLocation } from "react-router-dom";
import { Search } from "lucide-react";
import { NavItem } from "../ui/NavItem";

export const HeaderNav = () => {
  const location = useLocation();

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

    </nav>
  );
};

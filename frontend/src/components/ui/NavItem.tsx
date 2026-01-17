import { Link } from "react-router-dom";
import React from "react";

interface NavItemProps {
  path: string;
  name: string;
  icon: React.ReactNode;
  isActive: boolean;
}

export const NavItem: React.FC<NavItemProps> = ({ path, name, icon, isActive }) => {
  return (
    <Link
      to={path}
      className={`flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-medium transition-all duration-200 ${
        isActive
          ? "bg-slate-800 text-white shadow-sm"
          : "text-slate-400 hover:bg-slate-800/50 hover:text-slate-200"
      }`}
    >
      {icon}
      <span>{name}</span>
    </Link>
  );
};

import { Link } from "react-router-dom";
import { Star } from "lucide-react";

interface UserMenuDropdownProps {
  email: string;
  onLogout: () => void;
}

export const UserMenuDropdown: React.FC<UserMenuDropdownProps> = ({ 
  email,
  onLogout,
}) => {
  
  return (
    <div className="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg py-1 ring-1 ring-black ring-opacity-5 z-50 transform origin-top-right transition-all duration-200 ease-out">

      {/* 이메일 표시 섹션 */}
      <div className="px-4 py-2 border-b border-gray-100">
        <p className="text-xs text-gray-500">Email</p>
        <p className="text-sm font-medium text-gray-900 truncate" title={email}>
          {email}
        </p>
      </div>

      {/*  관심 종목 이동 링크 */}
      <div className="py-1 border-b border-gray-100">
        <Link
          to="/watchlist"
          className="flex items-center gap-2 px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 transition-colors w-full text-left"
        >
          <Star size={16} className="text-gray-500" />
          <span>관심 종목</span>
        </Link>
      </div>

      {/* 로그아웃 버튼 */}
      <button
        onClick={onLogout}
        className="w-full text-left block px-4 py-2 text-sm text-red-600 hover:bg-red-50 transition-colors"
      >
        로그아웃
      </button>
      
    </div>
  );
};

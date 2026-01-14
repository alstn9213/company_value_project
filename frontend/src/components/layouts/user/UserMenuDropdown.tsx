interface UserMenuDropdownProps {
  email: string;
  onLogout: () => void;
}

export const UserMenuDropdown: React.FC<UserMenuDropdownProps> = ({ email, onLogout }) => {
  return (
    <div className="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg py-1 ring-1 ring-black ring-opacity-5 z-50 transform origin-top-right transition-all duration-200 ease-out">
      {/* User Info Section */}
      <div className="px-4 py-2 border-b border-gray-100">
        <p className="text-xs text-gray-500">Signed in as</p>
        <p className="text-sm font-medium text-gray-900 truncate" title={email}>
          {email}
        </p>
      </div>

      {/* Action Buttons Section */}
      <button
        onClick={onLogout}
        className="w-full text-left block px-4 py-2 text-sm text-red-600 hover:bg-red-50 transition-colors"
      >
        로그아웃
      </button>
    </div>
  );
};

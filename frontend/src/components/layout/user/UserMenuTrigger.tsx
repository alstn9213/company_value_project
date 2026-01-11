interface UserMenuTriggerProps {
  username: string;
  isOpen: boolean;
  onClick: () => void;
}

const UserMenuTrigger: React.FC<UserMenuTriggerProps> = ({ username, isOpen, onClick }) => {
  return (
    <button
      onClick={onClick}
      className="flex items-center space-x-2 p-2 rounded-lg hover:bg-gray-100 transition-colors duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
      aria-expanded={isOpen}
      aria-haspopup="true"
    >
      {/* Avatar Circle */}
      <div className="w-8 h-8 rounded-full bg-blue-100 flex items-center justify-center text-blue-600 font-semibold">
        {username.charAt(0).toUpperCase()}
      </div>

      {/* Username Text */}
      <span className="text-sm font-medium text-gray-700 hidden md:block">
        {username}
      </span>

      {/* Chevron Icon */}
      <svg
        className={`w-4 h-4 text-gray-500 transition-transform duration-200 ${
          isOpen ? 'transform rotate-180' : ''
        }`}
        fill="none"
        stroke="currentColor"
        viewBox="0 0 24 24"
      >
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
      </svg>
    </button>
  );
};

export default UserMenuTrigger;
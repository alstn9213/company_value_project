import { Link } from "react-router-dom";

export const AuthActions = () => {
  return (
    <div className="flex items-center gap-2">
      <Link
        to="/login"
        className="px-4 py-2 rounded-lg text-sm font-medium transition-all duration-200 text-slate-400 hover:bg-slate-800/50 hover:text-slate-200"
      >
        로그인
      </Link>
      <Link
        to="/signup"
        className="px-4 py-2 rounded-lg text-sm font-medium transition-all duration-200 bg-blue-600 text-white hover:bg-blue-700"
      >
        회원가입
      </Link>
    </div>
  );
};

import { Link } from "react-router-dom";

const AuthFooter = () => {
  return (
    <div className="mt-6 text-center text-sm text-slate-500">
      계정이 없으신가요?{" "}
      <Link 
        to="/signup" 
        className="text-blue-400 cursor-pointer hover:underline"
      >
        회원가입
      </Link>
    </div>
  );
};

export default AuthFooter;
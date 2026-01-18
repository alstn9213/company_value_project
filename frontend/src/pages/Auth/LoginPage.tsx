import { Link } from "react-router-dom";
import { LoginForm } from "../../features/auth";

const LoginPage = () => {
  return (
    <div className="min-h-screen flex items-center justify-center bg-dark text-white">
      {/* 카드 레이아웃 컨테이너 */}
      <div className="w-full max-w-md p-8 bg-card rounded-xl shadow-lg border border-slate-700 backdrop-blur-sm">
        
        <div className="space-y-2 text-center">
          <h2 className="text-3xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-blue-400 to-emerald-400">
            Company Value
          </h2>
          <p className="text-slate-400">
            로그인
          </p>
        </div>

        <LoginForm />

        <div className="mt-6 text-center text-sm text-slate-500">
          계정이 없으신가요?{" "}
          <Link 
            to="/signup" 
            className="text-blue-400 cursor-pointer hover:underline"
          >
            회원가입
          </Link>
        </div>
        
      </div>
    </div>
  );
};

export default LoginPage;

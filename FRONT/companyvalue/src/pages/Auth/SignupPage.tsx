import { Link } from "react-router-dom"
import { SignupForm } from "../../features/auth/components/SignupForm";

const SignupPage = () => {
  return (
    <div className="min-h-screen flex items-center justify-center bg-dark text-white p-4">
      <div className="w-full max-w-md p-8 space-y-6 bg-card rounded-xl shadow-lg border border-slate-700 backdrop-blur-sm">
        
        {/* 헤더 섹션 */}
        <div className="text-center space-y-2">
          <h2 className="text-3xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-blue-400 to-emerald-400">
            Sign Up
          </h2>
          <p className="text-slate-400">새로운 계정을 생성합니다</p>
        </div>

        {/* 폼 섹션 */}
        <SignupForm />

        {/* 푸터 섹션 */}
        <div className="text-center text-sm text-slate-500 pt-2">
          이미 계정이 있으신가요?{' '}
          <Link 
            to="/login" 
            className="text-emerald-400 hover:text-emerald-300 hover:underline font-medium transition-colors"
          >
            로그인
          </Link>
        </div>
        
      </div>
    </div>
  );
};

export default SignupPage;
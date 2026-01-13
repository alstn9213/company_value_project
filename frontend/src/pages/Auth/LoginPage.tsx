import LoginHeader from "../../features/auth/ui/LoginHeader";
import LoginForm from "../../features/auth/ui/LoginForm";
import AuthFooter from "../../features/auth/ui/AuthFooter";

const LoginPage = () => {
  return (
    <div className="min-h-screen flex items-center justify-center bg-dark text-white">
      {/* 카드 레이아웃 컨테이너 */}
      <div className="w-full max-w-md p-8 bg-card rounded-xl shadow-lg border border-slate-700 backdrop-blur-sm">
        
        <LoginHeader />
        <LoginForm />
        <AuthFooter />
        
      </div>
    </div>
  );
};

export default LoginPage;

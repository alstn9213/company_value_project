import { useState } from "react";
import { useAuthStore } from "../../stores/authStore";
import { useNavigate } from "react-router-dom";
import { authApi } from "../../api/authApi";

const LoginPage = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const login = useAuthStore((state) => state.login);
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      // 1. 백엔드 API 호출
      const data = await authApi.login({ email, password });

      // 2. 전역 스토어 및 로컬스토리지에 저장
      login(data.accessToken, data.nickname, email);

      // 3. 메인 페이지로 이동
      navigate("/");
    } catch (err) {
      setError("이메일 또는 비밀번호를 확인해주세요.");
      console.error(err);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-dark text-white">
      <div className="w-full max-w-md p-8 space-y-6 bg-card rounded-xl shadow-lg border border-slate-700 backdrop-blur-sm">
        <h2 className="text-3xl font-bold text-center text-transparent bg-clip-text bg-gradient-to-r from-blue-400 to-emerald-400">
          Company Value
        </h2>
        <p className="text-center text-slate-400">
          로그인하여 투자를 시작하세요
        </p>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-slate-300">
              Email
            </label>
            <input
              type="email"
              required
              className="w-full px-4 py-2 mt-1 bg-slate-800 border border-slate-600 rounded-lg focus:ring-2 focus:ring-emerald-500 focus:outline-none text-white"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-slate-300">
              Password
            </label>
            <input
              type="password"
              required
              className="w-full px-4 py-2 mt-1 bg-slate-800 border border-slate-600 rounded-lg focus:ring-2 focus:ring-emerald-500 focus:outline-none text-white"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>

          {error && <p className="text-red-500 text-sm text-center">{error}</p>}

          <button
            type="submit"
            className="w-full py-3 font-bold text-white bg-emerald-600 rounded-lg hover:bg-emerald-500 transition-all shadow-lg shadow-emerald-500/20"
          >
            로그인
          </button>
        </form>

        <div className="text-center text-sm text-slate-500">
          계정이 없으신가요?{" "}
          <span
            className="text-blue-400 cursor-pointer hover:underline"
            onClick={() => navigate("/signup")}
          >
            회원가입
          </span>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;

import { useState } from "react";
import { useNavigate } from "react-router-dom"
import { authApi } from "../../features/auth/api/authApi";

const SignupPage = () => {
  const navigate = useNavigate();

  // 입력 폼 상태 관리
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    confirmPassword: '',
    nickname: '',
  });

  const [error, setError] = useState('');

  // 입력 변경 핸들러
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  // 폼 제출 핸들러
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    // 1. 유효성 검사: 비밀번호 일치 여부 확인
    if(formData.password !== formData.confirmPassword) {
      setError('비밀번호가 일치하지 않습니다.');
      return;
    }

    try {
      // 2. 회원가입 API 호출
      await authApi.signup({
        email: formData.email,
        password: formData.password,
        nickname: formData.nickname,
      });

      // 3. 성공 시 로그인 페이지로 이동(알림 추가 가능)
      alert('회원가입이 완료되었습니다. 로그인해주세요.')
      navigate('/login');
    } catch(err: any) {
      console.error(err);
      // 백엔드에서 보낸 에러 메시지가 있다면 표시, 없으면 기본 메시지
      setError(err.response?.data || '회원가입 중 오류가 발생했습니다.');
    }
  };
  return (
    <div className="min-h-screen flex items-center justify-center bg-dark text-white">
      <div className="w-full max-w-md p-8 space-y-6 bg-card rounded-xl shadow-lg border border-slate-700 backdrop-blur-sm">
        <h2 className="text-3xl font-bold text-center text-transparent bg-clip-text bg-gradient-to-r from-blue-400 to-emerald-400">
          Sign Up
        </h2>
        <p className="text-center text-slate-400">새로운 계정을 생성합니다</p>

        <form onSubmit={handleSubmit} className="space-y-4">
          {/* 이메일 입력 */}
          <div>
            <label className="block text-sm font-medium text-slate-300">Email</label>
            <input
              type="email"
              name="email"
              required
              className="w-full px-4 py-2 mt-1 bg-slate-800 border border-slate-600 rounded-lg focus:ring-2 focus:ring-emerald-500 focus:outline-none text-white"
              value={formData.email}
              onChange={handleChange}
            />
          </div>

          {/* 닉네임 입력 */}
          <div>
            <label className="block text-sm font-medium text-slate-300">Nickname</label>
            <input
              type="text"
              name="nickname"
              required
              className="w-full px-4 py-2 mt-1 bg-slate-800 border border-slate-600 rounded-lg focus:ring-2 focus:ring-emerald-500 focus:outline-none text-white"
              value={formData.nickname}
              onChange={handleChange}
            />
          </div>

          {/* 비밀번호 입력 */}
          <div>
            <label className="block text-sm font-medium text-slate-300">Password</label>
            <input
              type="password"
              name="password"
              required
              className="w-full px-4 py-2 mt-1 bg-slate-800 border border-slate-600 rounded-lg focus:ring-2 focus:ring-emerald-500 focus:outline-none text-white"
              value={formData.password}
              onChange={handleChange}
            />
          </div>

          {/* 비밀번호 확인 */}
          <div>
            <label className="block text-sm font-medium text-slate-300">Confirm Password</label>
            <input
              type="password"
              name="confirmPassword"
              required
              className="w-full px-4 py-2 mt-1 bg-slate-800 border border-slate-600 rounded-lg focus:ring-2 focus:ring-emerald-500 focus:outline-none text-white"
              value={formData.confirmPassword}
              onChange={handleChange}
            />
          </div>

          {/* 에러 메시지 표시 */}
          {error && <p className="text-red-500 text-sm text-center font-medium">{error}</p>}

          <button
            type="submit"
            className="w-full py-3 font-bold text-white bg-emerald-600 rounded-lg hover:bg-emerald-500 transition-all shadow-lg shadow-emerald-500/20"
          >
            회원가입
          </button>
        </form>

        <div className="text-center text-sm text-slate-500">
          이미 계정이 있으신가요?{' '}
          <span
            className="text-blue-400 cursor-pointer hover:underline"
            onClick={() => navigate('/login')}
          >
            로그인
          </span>
        </div>
      </div>
    </div>
  );
};

export default SignupPage;
import { useSignupForm } from "../hooks/useSignupForm";
import { AuthInput } from "./AuthInput";

export const SignupForm = () => {
  const { formData, error, isLoading, handleChange, handleSubmit } = useSignupForm();

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <AuthInput
        label="Email"
        type="email"
        name="email"
        required
        value={formData.email}
        onChange={handleChange}
        disabled={isLoading}
        placeholder="example@email.com"
      />

      <AuthInput
        label="Nickname"
        type="text"
        name="nickname"
        required
        value={formData.nickname}
        onChange={handleChange}
        disabled={isLoading}
        placeholder="사용하실 닉네임을 입력하세요"
      />

      <AuthInput
        label="Password"
        type="password"
        name="password"
        required
        value={formData.password}
        onChange={handleChange}
        disabled={isLoading}
        placeholder="********"
      />

      <AuthInput
        label="Confirm Password"
        type="password"
        name="confirmPassword"
        required
        value={formData.confirmPassword}
        onChange={handleChange}
        disabled={isLoading}
        placeholder="********"
      />

      {/* 폼 전체 에러 메시지 */}
      {error && (
        <div className="p-3 bg-red-500/10 border border-red-500/20 rounded-lg">
          <p className="text-red-400 text-sm text-center font-medium">{error}</p>
        </div>
      )}

      <button
        type="submit"
        disabled={isLoading}
        className={`w-full py-3 font-bold text-white bg-emerald-600 rounded-lg transition-all shadow-lg shadow-emerald-500/20 
          ${isLoading ? 'opacity-50 cursor-not-allowed' : 'hover:bg-emerald-500 hover:shadow-emerald-500/40'}`}
      >
        {isLoading ? '가입 처리 중...' : '회원가입'}
      </button>
    </form>
  );
};
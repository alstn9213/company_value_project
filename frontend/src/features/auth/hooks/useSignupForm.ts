import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { authApi } from "../api/authApi";

export const useSignupForm = () => {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    email: '',
    password: '',
    confirmPassword: '',
    nickname: '',
  });

  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false); // UX 개선을 위해 추가

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
    // 사용자가 다시 입력을 시작하면 에러 메시지 초기화
    if (error) setError('');
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    // 1. 유효성 검사
    if (formData.password !== formData.confirmPassword) {
      setError('비밀번호가 일치하지 않습니다.');
      return;
    }
    
    // 추가 유효성 검사 (빈 값 체크 등)
    if (!formData.email || !formData.nickname || !formData.password) {
        setError('모든 필드를 입력해주세요.');
        return;
    }

    setIsLoading(true);

    try {
      // 2. 회원가입 API 호출
      await authApi.signup({
        email: formData.email,
        password: formData.password,
        nickname: formData.nickname,
      });

      // 3. 성공 처리
      alert('회원가입이 완료되었습니다. 로그인해주세요.');
      navigate('/login');
    } catch (err: any) {
      console.error(err);
      // 백엔드 에러 메시지 처리 혹은 기본 메시지
      const errorMessage = err.response?.data?.message || err.response?.data || '회원가입 중 오류가 발생했습니다.';
      setError(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  return {
    formData,
    error,
    isLoading,
    handleChange,
    handleSubmit,
  };
};
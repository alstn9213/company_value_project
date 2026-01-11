import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuthStore } from "../../../stores/authStore";
import { authApi } from "../api/authApi";

export const useLoginForm = () => {
  const navigate = useNavigate();
  const login = useAuthStore((state) => state.login);

  // 입력 값을 객체로 관리하여 필드가 늘어나도 대응하기 쉽게 변경
  const [values, setValues] = useState({
    email: "",
    password: "",
  });
  
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false); // 로딩 상태 추가

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setValues((prev) => ({
      ...prev,
      [name]: value,
    }));
    // 에러 메시지가 있다면 입력 시 초기화하는 UX 개선
    if (error) setError("");
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    // 간단한 클라이언트 측 유효성 검사 (필요 시)
    if (!values.email || !values.password) {
      setError("이메일과 비밀번호를 모두 입력해주세요.");
      return;
    }

    setIsLoading(true);
    try {
      const data = await authApi.login({ email: values.email, password: values.password });
      
      // API 응답 구조에 따라 데이터 매핑
      // authStore의 login 함수 시그니처: (token, nickname, email) => void
      login(data.accessToken, data.nickname, values.email);
      
      navigate("/");
    } catch (err) {
      // 에러 로깅은 개발 환경에서만 보이도록 하거나 로거 활용 추천
      console.error(err);
      // 서버에서 내려주는 에러 메시지가 있다면 그것을 활용하는 것이 좋음 (예: err.response.data.message)
      setError("이메일 또는 비밀번호를 확인해주세요.");
    } finally {
      setIsLoading(false);
    }
  };

  return {
    values,
    error,
    isLoading,
    handleChange,
    handleSubmit,
  };
};
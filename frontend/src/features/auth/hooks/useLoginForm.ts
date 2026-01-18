import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuthStore } from "../../../stores/authStore";
import { authApi } from "../api/authApi";

export const useLoginForm = () => {
  const navigate = useNavigate();
  const login = useAuthStore((state) => state.login);

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
    if (error) { 
      setError("");
    }
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
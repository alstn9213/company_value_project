import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { AxiosError } from "axios";
import { useAuthStore } from "../../../stores/authStore";
import { authApi } from "../api/authApi";
import { ApiErrorData } from "../../../types/api";

export const useLoginForm = () => {
  const navigate = useNavigate();
  const login = useAuthStore((state) => state.login);

  const [values, setValues] = useState({
    email: "",
    password: "",
  });
  
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);

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
    
    if (!values.email || !values.password) {
      setError("이메일과 비밀번호를 모두 입력해주세요.");
      return;
    }

    setIsLoading(true);
    try {
      const data = await authApi.login({ email: values.email, password: values.password });
      
      // API 응답 구조에 따라 데이터 매핑
      login(data.accessToken, data.nickname, values.email);
      
      navigate("/");
    } catch (err) {
      console.error(err);
      const axiosError = err as AxiosError<ApiErrorData>;
      const message = axiosError.response?.data?.message || "이메일 또는 비밀번호를 확인해주세요.";
      setError(message);
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
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useMutation } from "@tanstack/react-query";
import { AxiosError } from "axios";
import { authApi } from "../api/authApi";
import { ApiErrorData, SignUpRequest } from "../../../types/auth";

export const useSignupForm = () => {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    email: "",
    password: "",
    confirmPassword: "",
    nickname: "",
  });
  
  // 클라이언트 측 유효성 검사 에러 (비밀번호 불일치 등)
  const [validationError, setValidationError] = useState("");

  const { mutate, isPending, error: apiError } = useMutation<
    string,
    AxiosError<ApiErrorData>,
    SignUpRequest
  >({
    mutationFn: (signupData) => authApi.signup(signupData),
    onSuccess: () => {
      alert("회원가입이 완료되었습니다. 로그인해주세요.");
      navigate("/login");
    },
    // onError는 useMutation의 error 상태에 자동으로 반영되므로,
    // 특별한 로직(에러 로깅 서비스에 전송)이 없다면 비워둬도 무방.
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
    // 사용자가 다시 입력을 시작하면 에러 메시지 초기화
    if (validationError) setValidationError("");
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setValidationError("");

    // 1. 유효성 검사
    if (formData.password !== formData.confirmPassword) {
      setValidationError("비밀번호가 일치하지 않습니다.");
      return;
    }

    const passwordRegex = /^(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z\d]{8,20}$/;
    if (!passwordRegex.test(formData.password)) {
      setValidationError("비밀번호는 영문, 숫자를 포함한 8~20자입니다.");
      return;
    }
    
    if (!formData.email || !formData.nickname || !formData.password) {
        setValidationError("모든 필드를 입력해주세요.");
        return;
    }

    // 2. 회원가입 API 호출 (useMutation 사용)
    mutate({
      email: formData.email,
      password: formData.password,
      nickname: formData.nickname,
    });
  };

  // API 에러 메시지 파싱
  const getApiErrorMessage = () => {
    if (!apiError) return null;

    const responseData = apiError.response?.data;

    // responseData가 문자열인 경우, 해당 문자열을 반환
    if (typeof responseData === "string") {
      return responseData;
    }

    // responseData가 message 속성을 가진 객체인 경우, message 값을 반환
    // 그렇지 않은 경우 기본 에러 메시지를 반환
    return responseData?.message || "회원가입 중 오류가 발생했습니다.";
  }

  return {
    formData,
    // 클라이언트 에러와 서버 에러를 통합하여 UI에 전달
    error: validationError || getApiErrorMessage(),
    isLoading: isPending,
    handleChange,
    handleSubmit,
  };
};
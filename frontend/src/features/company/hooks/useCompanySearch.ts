import { useState, useEffect } from "react";
import { AxiosError } from "axios";
import { companyApi } from "../api/companyApi";
import { CompanySummaryResponse } from "../../../types/company";
import { ApiErrorData } from "../../../types/auth";

export const useCompanySearch = (initialKeyword = "") => {
  const [keyword, setKeyword] = useState(initialKeyword);
  const [suggestions, setSuggestions] = useState<CompanySummaryResponse[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  // 에러 상태 추가
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchSuggestions = async () => {
      if (keyword.trim().length < 1) {
        setSuggestions([]);
        return;
      }

      setIsLoading(true);
      setError(null); // 새로운 검색 시작 시 에러 초기화
      try {
        const data = await companyApi.search(keyword);
        setSuggestions(data);
      } catch (err) {
        let errorMessage = "검색 중 알 수 없는 오류가 발생했습니다.";
        if (err instanceof AxiosError) {
          const responseData = err.response?.data as ApiErrorData;
          if (typeof responseData === "string") {
            errorMessage = responseData;
          } else if (responseData?.message) {
            errorMessage = responseData.message;
          }
        }
        setError(errorMessage);
        console.error("검색 실패:", errorMessage);
        setSuggestions([]);
      } finally {
        setIsLoading(false);
      }
    };

    // Debounce 300ms
    const timer = setTimeout(fetchSuggestions, 300);
    return () => clearTimeout(timer);
  }, [keyword]);

  const clearSearch = () => {
    setKeyword("");
    setSuggestions([]);
    setError(null);
  };

  return {
    keyword,
    setKeyword,
    suggestions,
    isLoading,
    error, // 컴포넌트에서 에러를 표시할 수 있도록 반환
    clearSearch,
  };
};
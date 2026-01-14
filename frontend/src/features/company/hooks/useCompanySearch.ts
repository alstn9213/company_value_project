import { useState, useEffect } from "react";
import { companyApi } from "../api/companyApi";
import { CompanySummaryResponse } from "../../../types/company";

export const useCompanySearch = (initialKeyword = "") => {
  const [keyword, setKeyword] = useState(initialKeyword);
  const [suggestions, setSuggestions] = useState<CompanySummaryResponse[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    const fetchSuggestions = async () => {
      if (keyword.trim().length < 1) {
        setSuggestions([]);
        return;
      }

      setIsLoading(true);
      try {
        const data = await companyApi.search(keyword);
        setSuggestions(data);
      } catch (error) {
        console.error("검색 실패:", error);
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
  };

  return {
    keyword,
    setKeyword,
    suggestions,
    isLoading, // 로딩 상태 추가 (UX 개선용)
    clearSearch,
  };
};
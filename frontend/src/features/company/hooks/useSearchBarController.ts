import { useState, useRef, useEffect, FormEvent } from "react";
import { useCompanySearch } from "./useCompanySearch"; 
import { useNavigate } from "react-router-dom";

export const useSearchBarController = () => {
  const navigate = useNavigate();
  const dropdownRef = useRef<HTMLDivElement>(null);
  const [showDropdown, setShowDropdown] = useState(false);
  const { keyword, setKeyword, suggestions, isLoading, clearSearch } = useCompanySearch();

  useEffect(() => {
    if (suggestions.length > 0) {
      setShowDropdown(true);
    }
  }, [suggestions]);

  // 외부 클릭 감지 (Dropdown 닫기)
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setShowDropdown(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const handleSearchSubmit = (e: FormEvent) => {
    e.preventDefault();
    if (keyword.trim()) {
      setShowDropdown(false);
      navigate(`/companies?search=${keyword}`);
    }
  };

  const handleSelectCompany = (ticker: string) => {
    clearSearch();
    setShowDropdown(false);
    navigate(`/company/${ticker}`);
  };

  const handleClear = () => {
    clearSearch();
  };

  const handleFocus = () => {
    if (suggestions.length > 0) { 
      setShowDropdown(true);
    }
  };

  return {
    keyword,
    setKeyword,
    suggestions,
    isLoading,
    showDropdown,
    dropdownRef,
    handleSearchSubmit,
    handleSelectCompany,
    handleClear,
    handleFocus,
  };
};
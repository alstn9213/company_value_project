import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Search, X } from "lucide-react";
import { useCompanySearch } from "../../hooks/useCompanySearch";
import { SearchSuggestions } from "./SearchSuggestions";

export const SearchBar = () => {
  const navigate = useNavigate();
  const dropdownRef = useRef<HTMLDivElement>(null);
  const [showDropdown, setShowDropdown] = useState(false);  
  const { keyword, setKeyword, suggestions, clearSearch } = useCompanySearch();

  // 드롭다운 노출 제어 (검색 결과가 있으면 자동 노출)
  useEffect(() => {
    if (suggestions.length > 0) setShowDropdown(true);
  }, [suggestions]);

  // 외부 클릭 시 드롭다운 닫기
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setShowDropdown(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const handleSearchSubmit = (e: React.FormEvent) => {
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

  return (
    <div className="relative w-full max-w-md hidden sm:block" ref={dropdownRef}>
      <form
        onSubmit={handleSearchSubmit}
        className="flex w-full items-center rounded-lg border border-slate-700 bg-slate-900 shadow-sm transition-colors focus-within:border-blue-500 focus-within:ring-1 focus-within:ring-blue-500"
      >
        <div className="pl-3 text-slate-500">
          <Search className="h-4 w-4" />
        </div>

        <input
          type="text"
          placeholder="기업명 또는 티커(AAPL) 검색"
          className="w-full bg-transparent border-none py-2.5 pl-2 pr-2 text-sm text-slate-200 placeholder-slate-500 focus:outline-none focus:ring-0"
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          onFocus={() => {
            if (suggestions.length > 0) setShowDropdown(true);
          }}
        />

        {keyword && (
          <button
            type="button"
            onClick={clearSearch}
            className="p-2 text-slate-500 hover:text-white"
          >
            <X size={14} />
          </button>
        )}

        <button
          type="submit"
          className="bg-blue-600 hover:bg-blue-500 text-white px-5 py-2.5 rounded-r-lg text-sm font-medium transition-colors m-[-1px] mr-[-1px]"
        >
          검색
        </button>
      </form>

      <SearchSuggestions 
        suggestions={suggestions} 
        onSelect={handleSelectCompany} 
        isVisible={showDropdown} 
      />
    </div>
  );
};
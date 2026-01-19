import { Loader2, Search, X } from "lucide-react";
import { SearchSuggestions } from "./SearchSuggestions";
import { useSearchBarController } from "../../hooks/useSearchBarController";

export const SearchBar = () => {
  const {
    keyword,
    setKeyword,
    suggestions,
    isLoading,
    showDropdown,
    dropdownRef,
    handleSearchSubmit,
    handleSelectCompany,
    handleClear,
    handleFocus
  } = useSearchBarController();

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
          onFocus={handleFocus}
        />

        {/* 로딩 표시 및 초기화 버튼 */}
        <div className="flex items-center pr-2 space-x-1">
          {isLoading && <Loader2 className="h-4 w-4 animate-spin text-blue-500" />}
          
          {keyword && !isLoading && (
            <button
              type="button"
              onClick={handleClear}
              className="p-1 text-slate-500 hover:text-white"
            >
              <X size={14} />
            </button>
          )}
        </div>

        <button
          type="submit"
          className="bg-blue-600 hover:bg-blue-500 text-white px-5 py-2.5 rounded-r-lg text-sm font-medium transition-colors m-[-1px] mr-[-1px]"
        >
          검색
        </button>
      </form>

      {/* 검색어 추천 드롭다운 */}
      <SearchSuggestions 
        suggestions={suggestions} 
        onSelect={handleSelectCompany} 
        isVisible={showDropdown} 
      />
    </div>
  );
};
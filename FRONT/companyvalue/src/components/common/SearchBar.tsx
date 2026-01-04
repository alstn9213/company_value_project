import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Company } from "../../types/company";
import { companyApi } from "../../api/companyApi";
import { Search, X } from "lucide-react";

const SearchBar = () => {
  const navigate = useNavigate();
  const [keyword, setKeyword] = useState("");
  const [suggestions, setSuggestions] = useState<Company[]>([]);
  const [showDropdown, setShowDropdown] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);   

// 검색어 입력 시 DB 조회 (Debounce 적용: 0.3초)
  useEffect(() => {
    const fetchSuggestions = async () => {
    if (keyword.trim().length < 1) {
        setSuggestions([]);
        return;
    }
    try {
        const data = await companyApi.search(keyword);
        setSuggestions(data);
        setShowDropdown(true);
    } catch (error) {
        console.error("검색 실패:", error);
    }
    };

    const timer = setTimeout(fetchSuggestions, 300);
    return () => clearTimeout(timer);
}, [keyword]);

    // 드롭다운 외부 클릭 시 닫기
    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
        if (
            dropdownRef.current &&
            !dropdownRef.current.contains(event.target as Node)
        ) {
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
    setKeyword("");
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
                onClick={() => {
                setKeyword("");
                setSuggestions([]);
                }}
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

        {showDropdown && suggestions.length > 0 && (
            <div className="absolute top-full left-0 right-0 mt-2 bg-slate-800 border border-slate-700 rounded-lg shadow-xl overflow-hidden max-h-80 overflow-y-auto z-50">
            <ul>
                {suggestions.map((company) => (
                <li
                    key={company.ticker}
                    onClick={() => handleSelectCompany(company.ticker)}
                    className="px-4 py-3 hover:bg-slate-700 cursor-pointer flex justify-between items-center group transition-colors border-b border-slate-700/50 last:border-0"
                >
                    <div className="flex flex-col overflow-hidden">
                    <span className="font-bold text-white group-hover:text-blue-400 transition-colors">
                        {company.ticker}
                    </span>
                    <span className="text-xs text-slate-400 truncate">
                        {company.name}
                    </span>
                    </div>
                    <div className="flex flex-col items-end shrink-0">
                    <span className="text-[10px] bg-slate-900 text-slate-400 px-1.5 py-0.5 rounded border border-slate-700">
                        {company.exchange}
                    </span>
                    <span className="text-xs text-slate-500 mt-1">
                        {company.sector}
                    </span>
                    </div>
                </li>
                ))}
            </ul>
            </div>
        )}
        </div>
    );
    };

export default SearchBar;
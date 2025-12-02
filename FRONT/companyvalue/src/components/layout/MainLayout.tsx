import React, { useEffect, useRef, useState } from "react";
import { LogIn, LogOut, Search, Star, TrendingUp, X } from "lucide-react";
import { Link, Outlet, useLocation, useNavigate } from "react-router-dom";
import { useAuthStore } from "../../stores/authStore";
import { Company } from "../../types/company";
import { companyApi } from "../../api/companyApi";

const MainLayout = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { user, logout, isAuthenticated } = useAuthStore();

  // 검색 관련 상태 관리
  const [keyword, setKeyword] = useState("");
  const [suggestions, setSuggestions] = useState<Company[]>([]);
  const [showDropdown, setShowDropdown] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

  const menus = [
    { name: "기업 목록", path: "/companies", icon: <Search size={18} /> },
  ];

  const handleLogout = () => {
    if (confirm("로그아웃 하시겠습니까?")) {
      logout();
      navigate("/");
    }
  };

  // 검색어 입력 시 DB 조회 (Debounce 적용: 0.3초 대기)
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

  // 검색어 제출
  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (keyword.trim()) {
      setShowDropdown(false);
      navigate(`/companies?search=${keyword}`);
    }
  };

  // 드롭다운 항목 클릭 -> 기업 상세 페이지로 이동
  const handleSelectCompany = (ticker: string) => {
    setKeyword(""); // 검색어 초기화
    setShowDropdown(false);
    navigate(`/company/${ticker}`);
  };

  return (
    <div className="min-h-screen bg-[#0f172a] text-slate-200 font-sans flex flex-col">
      {/* 상단 통합 헤더 */}
      <header className="sticky top-0 z-50 w-full border-b border-slate-800 bg-[#0f172a]/90 backdrop-blur-md">
        <div className="mx-auto flex h-16 w-full items-center justify-between px-6 lg:px-10">
          
          {/* [Left] 로고 및 메인 메뉴 */}
          <div className="flex flex-1 items-center justify-start gap-8">
            <Link to="/" className="flex items-center gap-2 group shrink-0">
              <div className="rounded-lg bg-emerald-500/20 p-2 text-emerald-400 transition-colors group-hover:bg-emerald-500/30">
                <TrendingUp size={24} />
              </div>
              <span className="text-xl font-bold tracking-wide text-white">
                VALUE PICK
              </span>
            </Link>

            <nav className="hidden md:flex items-center gap-1">
              {menus.map((menu) => {
                const isActive = location.pathname === menu.path;
                return (
                  <Link
                    key={menu.path}
                    to={menu.path}
                    className={`flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-medium transition-all duration-200 ${
                      isActive
                        ? "bg-slate-800 text-white shadow-sm"
                        : "text-slate-400 hover:bg-slate-800/50 hover:text-slate-200"
                    }`}
                  >
                    {menu.icon}
                    <span>{menu.name}</span>
                  </Link>
                );
              })}
              {isAuthenticated && (
                <Link
                  to="/watchlist"
                  className={`flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-medium transition-all duration-200 ${
                    location.pathname === "/watchlist"
                      ? "bg-slate-800 text-white shadow-sm"
                      : "text-slate-400 hover:bg-slate-800/50 hover:text-slate-200"
                  }`}
                >
                  <Star size={18} />
                  <span>관심 종목</span>
                </Link>
              )}
            </nav>
          </div>

          {/* [Center] 검색바 UI 수정됨 */}
          <div className="flex flex-1 items-center justify-center z-50">
            <div className="relative w-full max-w-md hidden sm:block" ref={dropdownRef}>
              
              {/*
                form 태그에 border와 rounded를 적용하여 input과 button을 감쌌다.
                이렇게 하면 버튼이 튀어나오지 않고 하나의 박스처럼 보인다.
              */}
              <form 
                onSubmit={handleSearchSubmit} 
                className="flex w-full items-center rounded-lg border border-slate-700 bg-slate-900 shadow-sm transition-colors focus-within:border-blue-500 focus-within:ring-1 focus-within:ring-blue-500"
              >
                {/* 돋보기 아이콘 */}
                <div className="pl-3 text-slate-500">
                  <Search className="h-4 w-4" />
                </div>
                
                {/* 입력창 (테두리 제거, 배경 투명) */}
                <input
                  type="text"
                  placeholder="티커(AAPL) 또는 기업명 검색"
                  className="w-full bg-transparent border-none py-2.5 pl-2 pr-2 text-sm text-slate-200 placeholder-slate-500 focus:outline-none focus:ring-0"
                  value={keyword}
                  onChange={(e) => setKeyword(e.target.value)}
                  onFocus={() => {
                    if (suggestions.length > 0) setShowDropdown(true);
                  }}
                />

                {/* X 버튼 (입력값 있을 때만) */}
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

                {/* 검색 버튼 (우측 끝에 딱 맞게 배치) */}
                <button
                  type="submit"
                  className="bg-blue-600 hover:bg-blue-500 text-white px-5 py-2.5 rounded-r-lg text-sm font-medium transition-colors m-[-1px] mr-[-1px]"
                >
                  검색
                </button>
              </form>

              {/* 검색 결과 드롭다운 (DB 조회 결과) */}
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
          </div>

          {/* [Right] 유저 메뉴 */}
          <div className="flex flex-1 items-center justify-end gap-4">
            {isAuthenticated ? (
              <div className="flex items-center gap-4">
                <div className="hidden text-right sm:block">
                  <p className="text-xs text-slate-500">Welcome,</p>
                  <p className="text-sm font-bold text-emerald-400">
                    {user?.nickname || "User"}
                  </p>
                </div>
                <div className="h-8 w-[1px] bg-slate-800 hidden sm:block"></div>
                <button
                  onClick={handleLogout}
                  className="flex items-center gap-2 rounded-lg px-3 py-2 text-sm font-medium text-slate-400 transition-all hover:bg-red-500/10 hover:text-red-400"
                >
                  <LogOut size={18} />
                  <span className="hidden sm:inline">로그아웃</span>
                </button>
              </div>
            ) : (
              <div className="flex flex-row items-center gap-3">
                <Link
                  to="/login"
                  className="flex items-center gap-2 rounded-lg px-4 py-2 text-sm font-medium text-slate-300 hover:bg-slate-800 hover:text-white transition-colors"
                >
                  <LogIn size={18} />
                  <span>로그인</span>
                </Link>
                <Link
                  to="/signup"
                  className="whitespace-nowrap rounded-lg bg-blue-600 px-4 py-2 text-sm font-bold text-white shadow-lg shadow-blue-900/20 transition-all hover:bg-blue-500"
                >
                  회원가입
                </Link>
              </div>
            )}
          </div>

        </div>
      </header>

      <main className="flex-1 w-full relative">
        <div className="absolute top-0 left-0 w-full h-[300px] bg-gradient-to-b from-blue-900/5 to-transparent pointer-events-none" />
        <div className="relative z-0 w-full p-6 lg:p-8">
          <Outlet />
        </div>
      </main>
    </div>
  );
};

export default MainLayout;

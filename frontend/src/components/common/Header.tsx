import { SearchBar } from "../../features/company/components/search/SearchBar";
import { useAuthStore } from "../../stores/authStore";
import { Logo } from "../common/Logo";
import { AuthActions } from "../../features/auth/components/AuthActions";
import { UserMenu } from "../../features/auth/components/UserMenu";
import { HeaderNav } from "./HeaderNav";

export const Header = () => {
  const { isAuthenticated } = useAuthStore();

  return (
    <header className="sticky top-0 z-50 w-full border-b border-slate-800 bg-[#0f172a]/90 backdrop-blur-md">
      <div className="mx-auto flex h-16 w-full items-center justify-between px-6 lg:px-10">
        
        {/* [Left] 로고 및 네비게이션 */}
        <div className="flex flex-1 items-center justify-start gap-8">
          <Logo />
          <HeaderNav />
        </div>

        {/* [Center] 검색바 */}
        <div className="flex flex-1 items-center justify-center z-50">
          <SearchBar />
        </div>

        {/* [Right] 유저 메뉴 */}
        <div className="flex flex-1 items-center justify-end gap-2">
          {isAuthenticated ? <UserMenu /> : <AuthActions />}
        </div>
        
      </div>
    </header>
  );
};

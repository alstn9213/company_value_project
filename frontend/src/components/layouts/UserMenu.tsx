import { useAuthStore } from "../../stores/authStore";
import { useUserMenu } from "../../hooks/useUserMenu";
import { UserMenuTrigger } from "../UserMenuTrigger";
import { UserMenuDropdown } from "../UserMenuDropdown";

export const UserMenu: React.FC = () => {
  const { user } = useAuthStore();
  const { isOpen, menuRef } = useUserMenu();

  if (!user) return null;

 return (
    // ref는 외부 클릭 감지를 위해 최상위 컨테이너에 부착
    <div className="relative" ref={menuRef}>
      
      <UserMenuTrigger 
        username={user.nickname} 
      />

      {isOpen && (
        <UserMenuDropdown 
          email={user.email} 
        />
      )}
    </div>
  );
};



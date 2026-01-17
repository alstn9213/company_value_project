import { useCallback, useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuthStore } from "../stores/authStore";

export const useUserMenu = () => {
  const [isOpen, setIsOpen] = useState(false);
  const menuRef = useRef<HTMLDivElement>(null);
  const navigate = useNavigate();
  const { logout } = useAuthStore();

  // 메뉴 토글 핸들러
  const toggleMenu = useCallback(() => {
    setIsOpen((prev) => !prev);
  }, []);

  // 메뉴 닫기 핸들러
  const closeMenu = useCallback(() => {
    setIsOpen(false);
  }, []);

  // 로그아웃 핸들러
  const handleLogout = useCallback(() => {
    logout();
    closeMenu();
    navigate('/login');
  }, [logout, closeMenu, navigate]);

  // 외부 클릭 감지 로직 (Dropdown 닫기)
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
        closeMenu();
      }
    };

    if (isOpen) {
      document.addEventListener('mousedown', handleClickOutside);
    }

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
    
  }, [isOpen, closeMenu]);

  return {
    isOpen,
    menuRef,
    toggleMenu,
    handleLogout,
  };
};
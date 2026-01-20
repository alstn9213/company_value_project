import React from "react";
import { Trash2 } from "lucide-react";

interface WatchlistDeleteButtonProps {
  onClick: () => void; 
}

export const WatchlistDeleteButton = ({ onClick }: WatchlistDeleteButtonProps) => {
  const handleClick = (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();    
    onClick();
  };

  return (
    <button
      onClick={handleClick}
      className="p-2 text-slate-500 hover:text-red-400 hover:bg-red-400/10 rounded-full transition-colors"
      title="목록에서 삭제"
      aria-label="관심 목록에서 삭제"
    >
      <Trash2 size={18} />
    </button>
  );
};
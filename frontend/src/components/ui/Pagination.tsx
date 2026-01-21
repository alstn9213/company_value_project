import { ChevronLeft, ChevronRight } from "lucide-react";

interface PaginationProps {
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
}

export const Pagination = ({ currentPage, totalPages, onPageChange }: PaginationProps) => {
  if (totalPages <= 1) {
    return null;
  }

  const pages = Array.from({ length: totalPages }, (_, i) => i + 1);

  return (
    <div className="flex items-center justify-center gap-2">
      <button
        onClick={() => onPageChange(currentPage - 1)}
        disabled={currentPage === 1}
        className="p-2 rounded-lg text-slate-400 hover:bg-slate-800 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
        aria-label="이전 페이지"
      >
        <ChevronLeft size={20} />
      </button>

      <div className="flex items-center gap-1">
        {pages.map((page) => (
          <button
            key={page}
            onClick={() => onPageChange(page)}
            className={`w-8 h-8 rounded-lg text-sm font-medium transition-colors ${
              currentPage === page
                ? "bg-blue-600 text-white shadow-lg shadow-blue-500/20"
                : "text-slate-400 hover:bg-slate-800 hover:text-slate-200"
            }`}
          >
            {page}
          </button>
        ))}
      </div>

      <button
        onClick={() => onPageChange(currentPage + 1)}
        disabled={currentPage === totalPages}
        className="p-2 rounded-lg text-slate-400 hover:bg-slate-800 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
        aria-label="다음 페이지"
      >
        <ChevronRight size={20} />
      </button>
    </div>
  );
};
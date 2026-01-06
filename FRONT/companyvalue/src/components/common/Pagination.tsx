import { ChevronLeft, ChevronRight } from "lucide-react";

interface PaginationProps {
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
  isPlaceholderData?: boolean; // 데이터 패칭 중 버튼 비활성화용
  isLastPage?: boolean;
}

const Pagination = ({
  currentPage,
  totalPages,
  onPageChange,
  isLastPage = false,
}: PaginationProps) => {
  return (
    <div className="flex justify-center items-center gap-4 mt-8 pt-8 border-t border-slate-800/50">
      <button
        onClick={() => onPageChange(Math.max(currentPage - 1, 0))}
        className="flex items-center gap-1 px-3 py-1.5 text-sm font-medium text-slate-300 bg-slate-800 border border-slate-600 rounded hover:bg-slate-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
      >
        <ChevronLeft size={14} /> Prev
      </button>

      <span className="text-slate-400 text-sm font-mono">
        {currentPage + 1} / {totalPages}
      </span>

      <button
        onClick={() => {
          if (!isLastPage) {
            onPageChange(currentPage + 1);
          }
        }}
        className="flex items-center gap-1 px-3 py-1.5 text-sm font-medium text-slate-300 bg-slate-800 border border-slate-600 rounded hover:bg-slate-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
      >
        Next <ChevronRight size={14} />
      </button>
    </div>
  );
};

export default Pagination;
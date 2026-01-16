import { TermDefinition } from "../types/term";

interface TermWithTooltipProps {
  label: string;
  term?: TermDefinition;
  className?: string; // 라벨 텍스트 스타일 커스텀을 위해
}

export const TermWithTooltip = ({ 
  label, 
  term, 
  className = "text-slate-400", 
}: TermWithTooltipProps) => {
  return (
    <div className="flex items-center gap-1.5 group relative cursor-help w-fit">
      {/* 커스텀 스타일 */}
      <span className={`border-b border-dotted border-slate-600 transition-colors group-hover:border-slate-400 group-hover:text-white ${className}`}>
        {/* 라벨 텍스트  */}
        {label}
      </span>
    
      {/* 툴팁 (Hover) */}
      {term && (
        <div className="absolute bottom-full left-0 mb-2 w-72 p-4 bg-slate-900/95 border border-slate-700 rounded-lg shadow-xl backdrop-blur-md z-50 hidden group-hover:block animate-in fade-in zoom-in-95 duration-200 pointer-events-none text-left">
          {/* 용어 제목 */}
          <h4 className="font-bold text-slate-100 mb-2 text-sm">
            {term.title}
          </h4>
          {/* 용어 설명 */}
          <p className="text-xs text-slate-300 leading-relaxed whitespace-pre-line font-normal">
            {term.description}
          </p>
        </div>
      )}
    </div>
  );
};

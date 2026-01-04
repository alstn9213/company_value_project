import { HelpCircle } from "lucide-react";
import { TermDefinition } from "../../pages/company_detail/constants/financialTerms";

interface Props {
  label: string;
  term?: TermDefinition;
  className?: string; // 라벨 텍스트 스타일 커스텀을 위해
  iconClassName?: string; // 아이콘 스타일 커스텀
}

const TermWithTooltip = ({ 
  label, 
  term, 
  className = "text-slate-400", 
  iconClassName = "text-slate-600 group-hover:text-slate-400" 
}: Props) => {
  return (
    <div className="flex items-center gap-1.5 group relative cursor-help w-fit">
      {/* 라벨 텍스트 */}
      <span
        className={`border-b border-dotted border-slate-600 transition-colors group-hover:border-slate-400 group-hover:text-white ${className}`}
      >
        {label}
      </span>
      
      {/* 아이콘 */}
      <HelpCircle
        size={12}
        className={`transition-colors ${iconClassName}`}
      />

      {/* 툴팁 (Hover) */}
      {term && (
        <div className="absolute bottom-full left-0 mb-2 w-72 p-4 bg-slate-900/95 border border-slate-700 rounded-lg shadow-xl backdrop-blur-md z-50 hidden group-hover:block animate-in fade-in zoom-in-95 duration-200 pointer-events-none text-left">
          <h4 className="font-bold text-slate-100 mb-2 text-sm">
            {term.title}
          </h4>
          <p className="text-xs text-slate-300 leading-relaxed whitespace-pre-line font-normal">
            {term.description}
          </p>
          {/* 말풍선 꼬리 */}
          <div className="absolute top-full left-6 -mt-1.5 border-4 border-transparent border-t-slate-700" />
        </div>
      )}
    </div>
  );
};

export default TermWithTooltip;
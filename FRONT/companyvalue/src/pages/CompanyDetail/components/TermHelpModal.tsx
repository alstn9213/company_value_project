import { BookOpen, X } from "lucide-react";
import { FINANCIAL_TERMS } from "../constants/financialTerms";

interface Props {
  termKey: string | null; // null이면 모달 닫힘
  onClose: () => void;
}

const TermHelpModal = ({termKey, onClose}: Props) => {
  if(!termKey || !FINANCIAL_TERMS[termKey]) return null;
  const {title, description} = FINANCIAL_TERMS[termKey];

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4 animate-in fade-in duration-200"
      onClick={onClose}
    >
      <div
        className="bg-slate-900 border border-slate-700 p-6 rounded-2xl max-w-md w-full shadow-2xl relative animate-in zoom-in-95 duration-200"
        onClick={(e) => e.stopPropagation()} // 내부 클릭 시 닫힘 방지
      >
        {/* 닫기 버튼 */}
        <button
          onClick={onClose}
          className="absolute top-4 right-4 text-slate-400 hover:text-white transition-colors bg-slate-800/50 hover:bg-slate-800 rounded-full p-1"
        >
          <X size={20} />
        </button>

        {/* 헤더 */}
        <div className="flex items-center gap-3 mb-4">
          <div className="p-3 bg-emerald-500/10 rounded-xl border border-emerald-500/20">
            <BookOpen size={24} className="text-emerald-400" />
          </div>
          <h3 className="text-xl font-bold text-white leading-tight">
            {title}
          </h3>
        </div>

        {/* 설명 본문 */}
        <div className="bg-slate-800/50 rounded-xl p-4 border border-slate-700/50">
          <p className="text-slate-300 leading-relaxed whitespace-pre-wrap text-sm md:text-base">
            {description}
          </p>
        </div>

        {/* 하단 닫기 버튼 (모바일 편의성) */}
        <button
          onClick={onClose}
          className="w-full mt-5 py-3 bg-slate-800 hover:bg-slate-700 text-slate-200 font-medium rounded-xl transition-colors"
        >
          확인했습니다
        </button>
      </div>
    </div>
  );
};

export default TermHelpModal;
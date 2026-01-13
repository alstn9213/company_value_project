import { HelpCircle } from "lucide-react";

export const ChartGuide = () => {
  return (
    <div className="rounded-xl border border-slate-700 bg-white/5 p-6">
      <h3 className="mb-4 flex items-center gap-2 text-lg font-bold text-slate-200">
        <HelpCircle className="text-emerald-400" size={20} />
        차트 읽는 법
      </h3>
      
      <ul className="space-y-3 text-sm text-slate-300">
        <li className="flex items-start gap-3">
          {/* 시각적 요소를 아이콘처럼 표현하여 직관성 높임 */}
          <span className="mt-1.5 h-3 w-3 shrink-0 rounded-full bg-red-500/50 shadow-[0_0_8px_rgba(239,68,68,0.5)]"></span>
          <p className="leading-relaxed">
            <span className="font-bold text-red-400">붉은 배경 영역</span>은 
            장단기 금리차가 역전된 구간으로, <br className="hidden md:block"/>
            역사적으로 <strong className="text-slate-100">경기 침체가 임박했음</strong>을 알리는 위험 신호입니다.
          </p>
        </li>

        <li className="flex items-start gap-3">
          <span className="mt-1.5 h-3 w-3 shrink-0 rounded-full bg-blue-400 shadow-[0_0_8px_rgba(96,165,250,0.5)]"></span>
          <p className="leading-relaxed">
            <span className="font-bold text-blue-400">10년물 금리</span>가 
            4.0%를 초과하는 구간은 채권의 매력도가 높아져, 상대적으로 <strong className="text-slate-100">주식 시장의 매력도가 감소</strong>할 수 있습니다.
          </p>
        </li>

        <li className="mt-4 border-t border-slate-700/50 pt-4 text-xs text-slate-400">
          * 이 대시보드는 거시 경제 흐름을 통해 <strong>"지금이 공격적으로 투자할 때인지"</strong> 판단하는 것을 돕습니다.
        </li>
      </ul>
    </div>
  );
};


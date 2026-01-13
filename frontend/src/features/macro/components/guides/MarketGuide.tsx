import { Info } from "lucide-react";

export const MarketGuide = () => {
    return(
      <div className="rounded-xl border border-slate-700 bg-white/5 p-6">
        <h3 className="mb-4 flex items-center gap-2 text-lg font-bold text-slate-200">
          <Info className="text-blue-400" size={20} />
          시장 지표 해석 가이드
        </h3>
        <div className="grid grid-row-1 gap-6 md:grid-row-2 text-sm text-slate-300">
          <div className="space-y-2">
            <strong className="block text-emerald-400">장단기 금리 역전</strong>
            <p className="leading-relaxed opacity-90">
              보통은 장기 국채(10년) 금리가 단기(2년)보다 높지만
              경제 침체 우려가 커지면 단기 금리가 더 높아지는 <span className="font-bold text-red-400">'역전 현상'</span>이 발생합니다. 
            </p>
          </div>
          <div className="space-y-2">
            <strong className="block text-red-400">인플레이션 (CPI)</strong>
            <p className="leading-relaxed opacity-90">
              물가가 오르면 연준이 금리를 인상하여 돈을 흡수하는데,
              이 과정에서 금리 역전이 발생하기 쉽습니다. 
              즉, 높은 인플레이션은 경기 둔화로 이어질 수 있습니다.
            </p>
          </div>
        </div>
      </div>
    );
}

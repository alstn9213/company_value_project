import { Info } from "lucide-react";

const MarketGuide = () => {
    return(
 <div className="rounded-xl border border-slate-700 bg-white/5 p-6">
            <h3 className="mb-4 flex items-center gap-2 text-lg font-bold text-slate-200">
              <Info className="text-blue-400" size={20} />
              시장 지표 해석 가이드
            </h3>
            <div className="grid grid-cols-1 gap-6 md:grid-cols-2 text-sm text-slate-300">
              <div className="space-y-2">
                <strong className="block text-emerald-400">장단기 금리 역전</strong>
                <p className="leading-relaxed opacity-90">
                  보통은 장기 국채(10년) 금리가 단기(2년)보다 높아야 정상이지만,
                  경제 위기 우려가 커지면 단기 금리가 더 높아지는 <span className="font-bold text-red-400">'역전 현상'</span>이 발생합니다. 
                  이는 역사적으로 <span className="underline decoration-red-500/50 underline-offset-4">경기 침체의 가장 강력한 선행 지표</span>입니다.
                </p>
              </div>
              <div className="space-y-2">
                <strong className="block text-red-400">인플레이션 (CPI)</strong>
                <p className="leading-relaxed opacity-90">
                  물가가 오르면 연준이 금리를 인상하여 돈줄을 죄게 됩니다. 
                  이 과정에서 단기 금리가 급등하며 금리 역전이 발생하기 쉽습니다. 
                  즉, <span className="font-bold text-slate-100">높은 인플레이션은 경기 둔화의 방아쇠</span>가 될 수 있습니다.
                </p>
              </div>
            </div>
          </div>
    );
}

export default MarketGuide;
import { AlertTriangle, Info, TrendingDown } from "lucide-react";

const DashboardGuide = () => {
  return (
    <div className="bg-slate-800/50 border border-slate-700 rounded-xl p-6 mb-8 backdrop-blur-sm">
      <h3 className="text-lg font-bold text-white mb-4 flex items-center gap-2">
        <Info className="text-blue-400" size={20} />
        시장 지표 해석 가이드
      </h3>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 text-sm text-slate-300">
        {/* 1. 장단기 금리 역전 */}
        <div className="space-y-2">
          <h4 className="font-semibold text-emerald-400 flex items-center gap-2">
            <TrendingDown size={16} /> 장단기 금리 역전이란?
          </h4>
          <p className="leading-relaxed">
            보통은 장기 국채(10년) 금리가 단기(2년)보다 높아야 정상이지만, 경제
            위기 우려가 커지면 단기 금리가 더 높아지는{" "}
            <strong>'역전 현상'</strong>이 발생합니다. 이는 역사적으로{" "}
            <span className="text-red-400 font-bold">
              경기 침체의 가장 강력한 선행 지표
            </span>
            입니다.
          </p>
        </div>

        {/* 2. 인플레이션과의 관계 */}
        <div className="space-y-2">
          <h4 className="font-semibold text-emerald-400 flex items-center gap-2">
            <AlertTriangle size={16} /> 인플레이션과의 관계
          </h4>
          <p className="leading-relaxed">
            물가(CPI)가 오르면 연준은 금리를 인상하여 돈줄을 죄게 됩니다. 이
            과정에서 단기 금리가 급등하며 금리 역전이 발생하기 쉽습니다. 즉,{" "}
            <strong>높은 인플레이션은 경기 둔화의 방아쇠</strong>가 될 수
            있습니다.
          </p>
        </div>

        {/* 3. 차트 읽는 법 */}
        <div className="space-y-2">
          <h4 className="font-semibold text-emerald-400 flex items-center gap-2">
            📊 차트 활용법
          </h4>
          <ul className="list-disc list-inside space-y-1 ml-1">
            <li>
              <span className="text-red-400">붉은 배경 영역</span>: 장단기
              금리차 역전 구간(위험 신호)
            </li>
            <li>
              <span className="text-blue-400">10년물 금리</span> 4.0% 이상: 주식
              매력도 감소 구간
            </li>
            <li>
              이 대시보드는 거시 경제 흐름을 통해{" "}
              <strong>"지금이 공격적으로 투자할 때인지"</strong> 판단을
              돕습니다.
            </li>
          </ul>
        </div>
      </div>
    </div>
  );
};

export default DashboardGuide;

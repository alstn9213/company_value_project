import { ChartDataPoint } from "../types/chartDataPoint";

interface CustomTooltipProps {
  active?: boolean;
  payload?: {
    value: number;       // 차트 값 (여기선 환산 점수)
    payload: ChartDataPoint; // 원본 데이터 객체
  }[];
  label?: string;
}

export const CustomTooltip = ({active, payload}: CustomTooltipProps) => {
  if (active && payload && payload.length) {
    // payload[0].payload는 any 타입일 수 있으므로 ChartDataPoint로 단언
    const data = payload[0].payload as ChartDataPoint;
    
    return (
      <div className="bg-slate-900/95 border border-slate-700 p-3 rounded-lg shadow-xl backdrop-blur-md">
        <p className="font-bold text-slate-100 mb-1">{data.subject}</p>
        <div className="text-sm">
          <span className="text-slate-400">점수: </span>
          <span className="text-emerald-400 font-bold ml-1">
            {data.score}
          </span>
          <span className="text-slate-600 mx-1">/</span>
          <span className="text-slate-500">{data.fullMark}</span>
        </div>
        <div className="text-xs text-slate-600 mt-1">
          (100점 환산: {Math.round((data.score / data.fullMark) * 100)}점)
        </div>
      </div>
    );
  }
  return null;
};
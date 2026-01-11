import { AlertTriangle } from "lucide-react";
import { MacroData } from "../../../../types/macro";

interface CustomTooltipProps {
  active?: boolean;
  payload?: {
    name?: string;
    value?: number | string;
    color?: string;
    payload: MacroData; // 원본 데이터 타입 명시
    dataKey?: string;
  }[];
  label?: string;
}

const CustomTooltip = ({ active, payload, label }: CustomTooltipProps) => {
  if (active && payload && payload.length) {
    // payload[0].payload 로 원본 데이터 접근 가능
    const data = payload[0].payload as MacroData;
    const isInverted = data.us10y < data.us2y;

    return (
      <div className="rounded-lg border border-slate-700 bg-slate-900/95 p-3 shadow-xl backdrop-blur-sm">
        {/* 날짜 헤더 */}
        <p className="mb-2 border-b border-slate-700 pb-2 text-sm font-bold text-slate-200">
          {label}
        </p>

        {/* 기본 데이터 표시 */}
        <div className="space-y-1 text-xs">
          {payload.map((entry, index) => (
            <div key={index} className="flex items-center gap-2">
              <div
                className="h-2 w-2 rounded-full"
                style={{ backgroundColor: entry.color }}
              />
              <span className="text-slate-400">{entry.name}:</span>
              <span className="font-mono font-medium text-slate-200">
                {entry.value}
                {entry.name?.toString().includes("CPI") ? "%" : "%"}
              </span>
            </div>
          ))}
        </div>

        {/* 금리 역전 시 경고 박스 노출 */}
        {isInverted && (
          <div className="mt-3 animate-pulse rounded border border-red-500/30 bg-red-500/10 p-2">
            <div className="flex items-start gap-2">
              <AlertTriangle className="mt-0.5 h-4 w-4 shrink-0 text-red-400" />
              <div>
                <p className="text-xs font-bold text-red-400">장단기 금리 역전</p>
                <p className="mt-1 text-[10px] leading-tight text-red-200/70">
                  경기 침체의 강력한 전조 현상입니다.
                  <br />
                  (10년물 금리 &lt; 2년물 금리)
                </p>
              </div>
            </div>
          </div>
        )}
      </div>
    );
  }

  return null;
};

export default CustomTooltip;
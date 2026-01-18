import { CartesianGrid, Legend, Line, LineChart, ReferenceArea, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";
import { EmptyState } from "../../../components/ui/EmptyState";
import { ErrorState } from "../../../components/ui/ErrorState";
import { useInversionIntervals } from "../hooks/useInversionIntervals";
import { useMacroHistory } from "../hooks/useMacroQueries";
import { EconomicChartSkeleton } from "../ui/skeleton/EconomicChartSkeleton";
import { CustomTooltip } from "./CustomTooltip";

export const EconomicChart = () => {
  const { history, error, isLoading } = useMacroHistory();
  const inversionIntervals = useInversionIntervals(history);

  if (isLoading) {
    return <EconomicChartSkeleton />;
  }

  if (error) {
    return (
      <ErrorState
        title="데이터를 불러올 수 없습니다"
        message="잠시 후 다시 시도해주세요."
        onRetry={() => window.location.reload()}
      />
    );
  }

  if (!history || history.length === 0) {
    return (
      <EmptyState
        title="데이터가 없습니다"
        description="현재 표시할 경제 지표 데이터가 없습니다."
      />
    );
  }

  return (
    <div className="min-h-[400px] flex-1 rounded-xl border border-slate-700 bg-slate-800/50 p-5 shadow-sm backdrop-blur-sm">
      <h3 className="mb-6 text-lg font-bold text-slate-200">
        미국의 주요 금리 및 인플레이션 추이 (최근 10년)
      </h3>

      <div className="relative h-[500px] w-full">
        <div className="absolute inset-0">
          <ResponsiveContainer width="100%" height="100%" debounce={50} minWidth={0} minHeight={0}>
            <LineChart data={history} margin={{ top: 20, right: 20, left: 0, bottom: 5 }}>
              <CartesianGrid strokeDasharray="3 3" stroke="#334155" vertical={false} />
              
              <XAxis
                dataKey="date"
                stroke="#94a3b8"
                tick={{ fill: "#94a3b8", fontSize: 12 }}
                tickFormatter={(val) => val.substring(0, 4)}
                minTickGap={100}
              />
              
              {/* 왼쪽 Y축: 금리 */}
              <YAxis
                yAxisId="left"
                stroke="#94a3b8"
                tick={{ fill: "#94a3b8", fontSize: 12 }}
                domain={[0, "auto"]}
              />
              
              {/* 오른쪽 Y축: 인플레이션 */}
              <YAxis
                yAxisId="right"
                orientation="right"
                stroke="#f87171"
                tick={{ fill: "#f87171", fontSize: 12 }}
                domain={["auto", "auto"]}
              />

              <Tooltip 
                content={<CustomTooltip />} 
                cursor={{ stroke: "#64748b", strokeWidth: 1 }} 
              />
              
              <Legend wrapperStyle={{ paddingTop: "10px" }} />

              {/* 역전 구간 표시 (비즈니스 로직에서 계산된 값 활용) */}
              {inversionIntervals.map((interval, i) => (
                <ReferenceArea
                  key={i}
                  yAxisId="left"
                  x1={interval.start}
                  x2={interval.end}
                  fill="#ef4444"
                  fillOpacity={0.15}
                />
              ))}

              <Line
                yAxisId="left"
                type="monotone"
                dataKey="us10y"
                name="10년물 국채"
                stroke="#60a5fa"
                strokeWidth={2}
                dot={false}
                activeDot={{ r: 6, strokeWidth: 0 }}
              />
              <Line
                yAxisId="left"
                type="monotone"
                dataKey="us2y"
                name="2년물 국채"
                stroke="#34d399"
                strokeWidth={2}
                dot={false}
                activeDot={{ r: 6, strokeWidth: 0 }}
              />
              <Line
                yAxisId="right"
                type="stepAfter"
                dataKey="inflation"
                name="CPI (물가)"
                stroke="#f87171"
                strokeWidth={2}
                dot={false}
                activeDot={{ r: 6, strokeWidth: 0 }}
              />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </div>

      <div className="mt-2 text-right text-xs text-slate-500">
        * 붉은색 영역: 장단기 금리차 역전 구간
      </div>
    </div>
  );
};

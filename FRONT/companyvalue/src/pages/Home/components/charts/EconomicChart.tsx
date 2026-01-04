import { useMemo } from "react";
import { CartesianGrid, Legend, Line, LineChart, ReferenceArea, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";
import { MacroData } from "../../../../types/macro";
import Skeleton from "../../../../components/common/Skeleton";

interface EconomicChartProps {
  history: MacroData[];
  isLoading?: boolean;
}

const EconomicChart = ({ history, isLoading = false }: EconomicChartProps) => {
     // 장단기 금리차 역전 구간 계산
  const inversionIntervals = useMemo(() => {
    if (!history || history.length === 0) return [];
    const intervals: { start: string; end: string }[] = [];
    let startTime: string | null = null;

    history.forEach((d, index) => {
      const isInverted = d.spread < 0;
      if (isInverted && !startTime) startTime = d.date;
      else if (!isInverted && startTime) {
        intervals.push({
          start: startTime,
          end: history[index - 1].date,
        });
        startTime = null;
      }
    });
    // 마지막 데이터까지 역전 상태가 지속된 경우 처리
    if (startTime) {
      intervals.push({
        start: startTime,
        end: history[history.length - 1].date,
      });
    }
    return intervals;
  }, [history]);

  if (isLoading) {
    return (
      <div className="flex-1 rounded-xl border border-slate-700 bg-slate-800/50 p-5 shadow-sm backdrop-blur-sm min-h-[400px]">
        {/* 제목 스켈레톤 */}
        <div className="mb-6">
          <Skeleton className="h-7 w-1/3 rounded-lg" />
        </div>

        {/* 차트 영역 스켈레톤 */}
        <div className="relative h-[500px] w-full">
          <Skeleton className="h-full w-full rounded-xl" />
        </div>

        {/* 하단 설명 텍스트 스켈레톤 */}
        <div className="mt-2 flex justify-end">
          <Skeleton className="h-4 w-1/4 rounded" />
        </div>
      </div>
    );
  }

    return(        
          <div className="flex-1 rounded-xl border border-slate-700 bg-slate-800/50 p-5 shadow-sm backdrop-blur-sm min-h-[400px]">
            <h3 className="mb-6 text-lg font-bold text-slate-200">
              미국의 주요 금리 및 인플레이션 추이 (최근 10년)
            </h3>
            {/* [Best Practice Fix] Recharts 초기 렌더링 크기 오류 방지 (The width(-1) and height(-1) 에러 해결)
               1. relative + h-[500px]: 부모 영역 높이 고정
               2. absolute + inset-0: 자식 영역이 부모 영역을 강제로 꽉 채우도록 설정 (Flex 계산에서 격리)
            */}
            <div className="relative h-[500px] w-full">
              <div className="absolute inset-0">
              {/* 수정 사항: minWidth={0} minHeight={0} 추가 
                  설명: 초기 렌더링 시 부모 크기가 잡히지 않았을 때(-1), 
                        에러를 뱉지 않고 0으로 처리하도록 강제함.
                */}
                <ResponsiveContainer 
                  width="100%" 
                  height="100%" 
                  debounce={50} 
                  minWidth={0} 
                  minHeight={0}
                >
                  <LineChart
                    data={history}
                    margin={{ top: 20, right: 20, left: 0, bottom: 5 }}
                  >
                    <CartesianGrid strokeDasharray="3 3" stroke="#334155" vertical={false} />
                    <XAxis
                      dataKey="date"
                      stroke="#94a3b8"
                      tick={{ fill: "#94a3b8", fontSize: 12 }}
                      tickFormatter={(val) => val.substring(0, 4)}
                      minTickGap={100}
                    />
                    <YAxis
                      yAxisId="left"
                      stroke="#94a3b8"
                      tick={{ fill: "#94a3b8", fontSize: 12 }}
                      domain={[0, 'auto']}
                    />
                    <YAxis
                      yAxisId="right"
                      orientation="right"
                      stroke="#f87171"
                      tick={{ fill: "#f87171", fontSize: 12 }}
                      domain={['auto', 'auto']}
                    />
                    <Tooltip
                      contentStyle={{
                        backgroundColor: "#1e293b",
                        borderColor: "#475569",
                        color: "#f1f5f9",
                      }}
                      position={{ x: 0, y: 0 }}
                    />
                    <Legend wrapperStyle={{ paddingTop: "10px" }} />

                    {inversionIntervals.map((interval, i) => (
                      <ReferenceArea
                        key={i}
                        yAxisId="left"
                        x1={interval.start}
                        x2={interval.end}
                        fill="#ef4444"
                        fillOpacity={0.3}
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
                    />
                    <Line
                      yAxisId="left"
                      type="monotone"
                      dataKey="us2y"
                      name="2년물 국채"
                      stroke="#34d399"
                      strokeWidth={2}
                      dot={false}
                    />
                    <Line
                      yAxisId="right"
                      type="stepAfter"
                      dataKey="inflation"
                      name="CPI (물가)"
                      stroke="#f87171"
                      strokeWidth={2}
                      dot={false}
                    />
                  </LineChart>
                </ResponsiveContainer>
              </div>
            </div>
            
            <div className="mt-2 text-right text-xs text-slate-500">
              * 붉은색 영역: 장단기 금리차 역전 구간 (경제 침체 위험)
            </div>

          </div>

    );
}

export default EconomicChart;
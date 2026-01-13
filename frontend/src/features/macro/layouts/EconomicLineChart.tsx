import { CartesianGrid, Legend, Line, LineChart, ReferenceArea, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";
import { MacroData } from "../../../types/macro";
import { CustomTooltip } from "../components/CustomTooltip";

interface Interval {
  start: string;
  end: string;
}

interface EconomicLineChartProps {
  data?: MacroData[];
  inversionIntervals: Interval[];
}

export const EconomicLineChart = ({ data, inversionIntervals }: EconomicLineChartProps) => {
  return (
    <div className="relative h-[500px] w-full">
      <div className="absolute inset-0">
        <ResponsiveContainer width="100%" height="100%" debounce={50} minWidth={0} minHeight={0}>
          <LineChart data={data} margin={{ top: 20, right: 20, left: 0, bottom: 5 }}>
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
              domain={[0, "auto"]}
            />
            <YAxis
              yAxisId="right"
              orientation="right"
              stroke="#f87171"
              tick={{ fill: "#f87171", fontSize: 12 }}
              domain={["auto", "auto"]}
            />

            <Tooltip content={<CustomTooltip />} cursor={{ stroke: "#64748b", strokeWidth: 1 }} />
            <Legend wrapperStyle={{ paddingTop: "10px" }} />

            {/* 역전 구간 표시 */}
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
  );
};

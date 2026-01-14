import {
  Area,
  AreaChart,
  CartesianGrid,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";
import dayjs from "dayjs";
import { StockHistoryResponse } from "../../../../types/company";

interface StockPriceChartProps {
  data: StockHistoryResponse[];
}

export const StockPriceChart = ({ data }: StockPriceChartProps) => {
  return (
    <div className="flex-1 w-full min-h-0">
      <ResponsiveContainer 
        width="100%" 
        height="100%" 
        minWidth={0} 
        minHeight={0}
      >
        <AreaChart data={data} margin={{ top: 10, right: 10, left: 0, bottom: 0 }}>
          <defs>
            <linearGradient id="colorPrice" x1="0" y1="0" x2="0" y2="1">
              <stop offset="5%" stopOpacity={0.3} />
              <stop offset="95%" stopOpacity={0} />
            </linearGradient>
          </defs>
          <CartesianGrid strokeDasharray="3 3" stroke="#334155" vertical={false} />
          <XAxis
            dataKey="date"
            stroke="#94a3b8"
            tick={{ fontSize: 12 }}
            tickFormatter={(val) => dayjs(val).format("MM/DD")}
            minTickGap={30}
          />
          <YAxis
            stroke="#94a3b8"
            domain={["auto", "auto"]}
            tick={{ fontSize: 12 }}
            width={50}
            tickFormatter={(val) => `$${val}`}
          />
          <Tooltip
            contentStyle={{
              backgroundColor: "#1e293b",
              borderColor: "#475569",
              color: "#f1f5f9",
            }}
            formatter={(value: number | undefined) => [
              value !== undefined ? `$${value}` : "-", 
              "주가"
            ]}
            labelFormatter={(label) => dayjs(label).format("YYYY년 MM월 DD일")}
          />
          <Area
            type="monotone"
            dataKey="close"
            strokeWidth={2}
            fillOpacity={1}
            fill="url(#colorPrice)"
          />
        </AreaChart>
      </ResponsiveContainer>
    </div>
  );
};


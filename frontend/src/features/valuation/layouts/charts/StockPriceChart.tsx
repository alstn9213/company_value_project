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
import { formatCurrency } from "../../../../utils/formatters";

interface Props {
  data: StockHistoryResponse[];
}

export const StockPriceChart = ({ data }: Props) => {
  // 데이터가 없을 경우 예외 처리
  if (!data || data.length === 0) {
    return (
      <div className="w-full h-[350px] bg-slate-800/30 rounded-xl flex items-center justify-center text-slate-500 border border-slate-700/50">
        주가 데이터가 없습니다.
      </div>
    );
  }

  // 최신순(오른쪽)이 되도록 정렬 (혹시 몰라 안전장치)
  // 백엔드에서 이미 오름차순(과거->미래)으로 주긴 함.
  const chartData = [...data].sort((a, b) => (a.date > b.date ? 1 : -1));
  const latestPrice = chartData[chartData.length - 1].close;
  const startPrice = chartData[0].close;

  // 상승/하락에 따라 색상 변경 (상승: 빨강/파랑, 하락: 파랑/빨강 - 여기선 미국식이니 상승=초록, 하락=빨강)
  const isRising = latestPrice >= startPrice;
  const strokeColor = isRising ? "#10b981" : "#ef4444"; // emerald-500 : red-500
  const fillColor = isRising ? "#10b981" : "#ef4444";

  return (
    <div className="w-full h-[350px] bg-slate-800/30 rounded-xl p-4 border border-slate-700/50 flex flex-col">
      <div className="flex justify-between items-start mb-4 pl-2 shrink-0">
        <h3 className="text-lg font-bold text-slate-200 border-l-4 border-emerald-500 pl-2">
          📈 주가 변동 (1년)
        </h3>
        <div className="text-right">
          <span className="text-2xl font-bold text-white">
            {formatCurrency(latestPrice)}
          </span>
          <span
            className={`text-sm block ${
              isRising ? "text-emerald-400" : "text-red-400"
            }`}
          >
            현재가
          </span>
        </div>
      </div>

      <div className="flex-1 w-full min-h-0">
        <ResponsiveContainer 
          width="100%" 
          height="100%" 
          minWidth={0} 
          minHeight={0}
        >
          <AreaChart data={chartData} margin={{ top: 10, right: 10, left: 0, bottom: 0 }}>
            <defs>
              <linearGradient id="colorPrice" x1="0" y1="0" x2="0" y2="1">
                <stop offset="5%" stopColor={fillColor} stopOpacity={0.3} />
                <stop offset="95%" stopColor={fillColor} stopOpacity={0} />
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
              stroke={strokeColor}
              strokeWidth={2}
              fillOpacity={1}
              fill="url(#colorPrice)"
            />
          </AreaChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
};


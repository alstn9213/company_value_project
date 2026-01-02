import {
  Bar,
  BarChart,
  CartesianGrid,
  Legend,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";
import { FinancialDetail } from "../../types/company";
import { formatCurrency } from "../../utils/formatters";

interface Props {
  data: FinancialDetail[];
}

const FinancialTrendChart = ({ data }: Props) => {
  // 과거 -> 최신 순으로 정렬 (차트는 왼쪽이 과거인게 자연스러움)
  const chartData = [...data]
    .reverse()
    .slice(-8) // 최근 8개 분기만 표시
    .map((item) => ({
      name: `${item.year} Q${item.quarter}`,
      Revenue: item.revenue,
      "Op. Profit": item.operatingProfit,
      "Net Income": item.netIncome,
    }));

  return (
    <div className="w-full h-[350px] bg-slate-800/30 rounded-xl p-4 border border-slate-700/50">
      <h3 className="text-lg font-bold text-slate-200 mb-4 pl-2 border-l-4 border-blue-500">
        💰 실적 추이 (최근 2년)
      </h3>
      <ResponsiveContainer 
        width="100%" 
        height="100%" 
        minWidth={0} 
        minHeight={0}
      >
        <BarChart
          data={chartData}
          margin={{ top: 10, right: 30, left: 0, bottom: 0 }}
        >
          <CartesianGrid
            strokeDasharray="3 3"
            stroke="#334155"
            vertical={false}
          />
          <XAxis dataKey="name" stroke="#94a3b8" tick={{ fontSize: 12 }} />
          <YAxis
            stroke="#94a3b8"
            tickFormatter={(val) => formatCurrency(val)}
            tick={{ fontSize: 12 }}
            width={60}
          />
          <Tooltip
            contentStyle={{
              backgroundColor: "#1e293b",
              borderColor: "#475569",
              color: "#f1f5f9",
            }}
            formatter={(value: number | undefined) => formatCurrency(value)}
          />
          <Legend wrapperStyle={{ paddingTop: "20px" }} />
          <Bar
            dataKey="Revenue"
            fill="#3b82f6"
            name="매출액"
            radius={[4, 4, 0, 0]}
          />
          <Bar
            dataKey="Op. Profit"
            fill="#10b981"
            name="영업이익"
            radius={[4, 4, 0, 0]}
          />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
};

export default FinancialTrendChart;

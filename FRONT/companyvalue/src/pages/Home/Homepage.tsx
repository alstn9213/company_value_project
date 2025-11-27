import { useQuery } from "@tanstack/react-query";
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
  ReferenceArea,
} from "recharts";
import { macroApi } from "../../api/macroApi";
import {
  TrendingDown,
  TrendingUp,
  Activity,
  DollarSign,
  Percent,
  Trophy,
  ArrowRight,
} from "lucide-react";
import DashboardGuide from "../../components/dashboard/DashboardGuide";
import axiosClient from "../../api/axiosClient";
import { Link } from "react-router-dom";
import { getGradeColor } from "../../utils/formatters";
import { ScoreResult } from "../../types/company";

const HomePage = () => {
  // 1. 데이터 페칭(React Query)
  const { data: latest, isLoading: isLatestLoading } = useQuery({
    queryKey: ["macroLatest"],
    queryFn: macroApi.getLatest,
  });

  const { data: history, isLoading: isHistoryLoading } = useQuery({
    queryKey: ["macroHistory"],
    queryFn: macroApi.getHistory,
  });

  if (isLatestLoading || isHistoryLoading) {
    return (
      <div className="text-center p-10 text-slate-400">
        데이터를 불러오는 중입니다...
      </div>
    );
  }

  if (!latest || !history) {
    return (
      <div className="text-center p-10 text-red-400">
        데이터를 불러올 수 없습니다.
      </div>
    );
  }

  // 2. 장단기 금리차 역전 구간 찾기(차트 배경 강조용)
  // history 데이터 중 spread가 음수인 구간의 시작과 끝을 찾음(간소화 로직)
  const recessionStart = history.find((d) => d.spread < 0)?.date;
  const recessionEnd = history.findLast((d) => d.spread < 0)?.date; // 마지막 역전 구간을 찾는다

  return (
    <div className="p-6 max-w-7xl mx-auto space-y-8 text-slate-200">
      {/* 헤더 */}
      <div>
        <h1 className="text-3xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-blue-400 to-emerald-400">
          Market Dashboard
        </h1>
        <p className="text-slate-400 mt-2">
          최신 거시 경제 지표와 시장 흐름을 파악하세요. (기준일: {latest.date})
        </p>
      </div>

      {/* 3. 요약 카드 섹션 */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <StatCard
          title="미 10년물 국채 금리"
          value={`${latest.us10y}%`}
          subValue="투자 심리 기준"
          icon={<Activity size={24} className="text-blue-400" />}
          trend={latest.us10y > 4.0 ? "high" : "neutral"}
        />
        <StatCard
          title="장단기 금리차 (10Y-2Y)"
          value={`${latest.spread}p`}
          subValue="경기 침체 선행 지표"
          icon={
            <TrendingDown
              size={24}
              className={
                latest.spread < 0 ? "text-red-500" : "text-emerald-400"
              }
            />
          }
          trend={latest.spread < 0 ? "danger" : "safe"}
        />
        <StatCard
          title="기준 금리 (Fed Rate)"
          value={`${latest.fedFundsRate}%`}
          subValue="연준 정책 금리"
          icon={<DollarSign size={24} className="text-yellow-400" />}
        />
        <StatCard
          title="인플레이션 (CPI)"
          value={`${latest.inflation}%`}
          subValue="소비자 물가 지수"
          icon={<Percent size={24} className="text-red-400" />}
        />
      </div>

      {/* 4. 메인 콘텐츠 영역 (차트 + 랭킹 리스트) */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8 h-full">
        {/* 좌측: 메인 차트 (2칸 차지) */}
        <div className="lg:col-span-2 bg-card border border-slate-700 rounded-xl p-6 shadow-lg backdrop-blur-sm flex flex-col">
        <h3 className="text-xl font-bold mb-6 flex items-center gap-2">
          <TrendingUp className="text-emerald-500" />
          주요 금리 및 인플레이션 추이 (최근 30일)
        </h3>

        <div className="flex-1 min-h-[400px] w-full">
          <ResponsiveContainer width="100%" height="100%">
            <LineChart
              data={history}
              margin={{ top: 5, right: 30, left: 0, bottom: 5 }}
            >
              <CartesianGrid strokeDasharray="3 3" stroke="#334155" />
              <XAxis
                dataKey="date"
                stroke="#94a3b8"
                tick={{ fill: "#94a3b8" }}
                tickFormatter={(val) => val.substring(5)} // 'MM-DD' 형태로 자르기
              />
              <YAxis
                stroke="#94a3b8"
                tick={{ fill: "#94a3b8" }}
                domain={["auto", "auto"]}
              />
              <Tooltip
                contentStyle={{
                  backgroundColor: "#1e293b",
                  borderColor: "#475569",
                  color: "#f1f5f9",
                }}
                itemStyle={{ color: "#f1f5f9" }}
              />
              <Legend />

              {/* 장단기 금리차 역전 구간 강조 (빨간 배경) */}
              {recessionStart && recessionEnd && (
                <ReferenceArea
                  x1={recessionStart}
                  x2={recessionEnd}
                  strokeOpacity={0.3}
                  fill="red"
                  fillOpacity={0.1}
                  label="금리 역전 구간"
                />
              )}

              {/* 10년물(Blue), 2년물(Green), 인플레이션(Red) */}
              <Line
                type="monotone"
                dataKey="us10y"
                name="10년물 국채"
                stroke="#60a5fa" // Blue-400
                strokeWidth={2}
                dot={false}
                connectNulls={true}
              />
              <Line
                type="monotone"
                dataKey="us2y"
                name="2년물 국채"
                stroke="#34d399" // Emerald-400
                strokeWidth={2}
                dot={false}
                connectNulls={true}
              />
              <Line
                type="monotone"
                dataKey="inflation"
                name="인플레이션"
                stroke="#f87171" // Red-400
                strokeWidth={2}
                dot={false}
                connectNulls={true}
              />
            </LineChart>
          </ResponsiveContainer>
          </div>
        </div>
        {/* 우측: 우량주 리스트 (1칸 차지) */}
        <div className="lg:col-span-1 h-full">
          <TopRankedList />
        </div>
      </div>
      <DashboardGuide />
    </div>
  );
};

// 내부용 통계 카드 컴포넌트
const StatCard = ({
  title,
  value,
  subValue,
  icon,
  trend = "neutral",
}: {
  title: string;
  value: string;
  subValue: string;
  icon: React.ReactNode;
  trend?: "safe" | "danger" | "high" | "neutral";
}) => {
  // 트렌드에 따른 테두리 색상 결정
  let borderColor = "border-slate-700";
  if (trend === "danger") borderColor = "border-red-500/50";
  if (trend === "safe") borderColor = "border-emerald-500/50";
  if (trend === "high") borderColor = "border-orange-500/50";

  return (
    <div
      className={`bg-card border ${borderColor} p-6 rounded-xl shadow-lg flex flex-col justify-between hover:transform hover:-translate-y-1 transition duration-200`}
    >
      <div className="flex justify-between items-start mb-4">
        <div>
          <p className="text-sm text-slate-400 font-medium">{title}</p>
          <h3 className="text-3xl font-bold text-white mt-1">{value}</h3>
        </div>
        <div className="p-3 bg-slate-800 rounded-lg border border-slate-600">
          {icon}
        </div>
      </div>
      <div className="text-xs text-slate-500">{subValue}</div>
    </div>
  );
};

// [내부 컴포넌트] 우량주 Top 5 리스트
const TopRankedList = () => {
  const { data: topCompanies, isLoading } = useQuery({
    queryKey: ["topRanked"],
    queryFn: async () => {
      // companyApi에 getTopRanked가 없다면 직접 호출
      const res = await axiosClient.get<ScoreResult[]>("/api/scores/top");
      return res.data;
    },
  });

  return (
    <div className="bg-gradient-to-br from-slate-800/50 to-slate-900/50 border border-slate-700 rounded-xl p-6 backdrop-blur-sm h-full flex flex-col">
      <div className="flex justify-between items-center mb-6">
        <h3 className="text-xl font-bold text-white flex items-center gap-2">
          <Trophy className="text-yellow-400" size={20} /> 우량주 Top 5
        </h3>
        <Link
          to="/companies"
          className="text-sm text-blue-400 hover:text-blue-300 flex items-center gap-1 transition-colors"
        >
          전체 보기 <ArrowRight size={16} />
        </Link>
      </div>

      {isLoading ? (
        <div className="flex-1 flex items-center justify-center text-slate-500">
          순위를 불러오는 중...
        </div>
      ) : (
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="text-slate-400 text-sm border-b border-slate-700">
                <th className="py-3 pl-2 font-medium">순위</th>
                <th className="py-3 font-medium">종목</th>
                <th className="py-3 font-medium text-center">등급</th>
                <th className="py-3 font-medium text-right pr-2">점수</th>
              </tr>
            </thead>
            <tbody className="text-slate-200">
              {topCompanies?.slice(0, 5).map((item, index) => (
                <tr
                  key={item.ticker}
                  className="border-b border-slate-700/50 hover:bg-slate-700/30 transition-colors group"
                >
                  <td className="py-4 pl-2 font-bold text-slate-500 group-hover:text-white transition-colors">
                    {index + 1}
                  </td>
                  <td className="py-4">
                    <Link to={`/company/${item.ticker}`} className="block">
                      <span className="font-bold text-white group-hover:text-blue-400 transition-colors">
                        {item.ticker}
                      </span>
                      <span className="text-xs text-slate-400 block truncate max-w-[100px]">
                        {item.name}
                      </span>
                    </Link>
                  </td>
                  <td className="py-4 text-center">
                    <span
                      className={`inline-block px-2 py-1 rounded text-xs font-bold ${getGradeColor(
                        item.grade
                      )}`}
                    >
                      {item.grade}
                    </span>
                  </td>
                  <td className="py-4 text-right pr-2 font-mono font-bold text-emerald-400">
                    {item.totalScore}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default HomePage;

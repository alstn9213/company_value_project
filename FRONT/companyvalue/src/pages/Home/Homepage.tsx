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
  Info,
  Activity,
  TrendingDown,
  Percent,
  DollarSign,
} from "lucide-react";
import axiosClient from "../../api/axiosClient";
import { Link } from "react-router-dom";
import { getGradeColor } from "../../utils/formatters";
import { ScoreResult } from "../../types/company";
import { useMemo } from "react";

const HomePage = () => {
  const { data: latest, isLoading: isLatestLoading } = useQuery({
    queryKey: ["macroLatest"],
    queryFn: macroApi.getLatest,
  });

  const { data: history, isLoading: isHistoryLoading } = useQuery({
    queryKey: ["macroHistory"],
    queryFn: macroApi.getHistory,
  });

  // 우량주 랭킹 데이터 조회
  const { data: topCompanies, isLoading: isRankLoading } = useQuery({
    queryKey: ["topRanked"],
    queryFn: async () => {
      const res = await axiosClient.get<ScoreResult[]>("/api/scores/top");
      return res.data;
    },
  });

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

  // 로딩 상태 통합
  if (isLatestLoading || isHistoryLoading || isRankLoading) {
    return (
      <div className="flex h-full items-center justify-center text-slate-400">
        <div className="flex flex-col items-center gap-2">
          <div className="h-8 w-8 animate-spin rounded-full border-2 border-slate-600 border-t-blue-500"></div>
          <span>데이터 분석 중...</span>
        </div>
      </div>
    );
  }

  if (!latest || !history) return null;
  return (
    //  w-full로 전체 너비 사용
    <div className="w-full space-y-6">
      {/* 3단 그리드 레이아웃 (Left: 2, Center: 7, Right: 3) */}
      <div className="grid grid-cols-1 gap-6 xl:grid-cols-12">
        
        {/* --- [Left Column] 주요 지표 목록 --- */}
        <section className="space-y-4 xl:col-span-2">
          <h2 className="text-lg font-bold text-slate-100 border-l-4 border-blue-500 pl-3">
            주요 지표
          </h2>
          <div className="flex flex-col gap-3">
            <IndicatorItem
              label="기준 금리"
              value={`${latest.fedFundsRate}%`}
              icon={<DollarSign size={16} />}
              color="text-slate-200"
            />
            <IndicatorItem
              label="10년 물 금리"
              value={`${latest.us10y}%`}
              icon={<Activity size={16} />}
              color={latest.us10y >= 4.0 ? "text-orange-400" : "text-blue-400"}
            />
            <IndicatorItem
              label="2년 물 금리"
              value={`${latest.us2y}%`}
              icon={<Activity size={16} />}
              color="text-emerald-400"
            />
            <IndicatorItem
              label="장단기 금리 역전"
              value={`${latest.spread} %p`}
              icon={<TrendingDown size={16} />}
              color={latest.spread < 0 ? "text-red-500" : "text-slate-400"}
              isAlert={latest.spread < 0}
            />
            <IndicatorItem
              label="인플레이션"
              value={`${latest.inflation}%`}
              icon={<Percent size={16} />}
              color="text-red-400"
            />
          </div>
        </section>

        {/* --- [Center Column] 메인 차트 & 해석 가이드 --- */}
        <section className="flex flex-col gap-6 xl:col-span-7">
          <div className="flex items-end justify-between">
            <h2 className="text-2xl font-bold text-slate-100">
              미국의 경제 상황
            </h2>
            <span className="text-sm text-slate-400">
              기준일 {latest.date}
            </span>
          </div>

          {/* 차트 영역 */}
          <div className="flex-1 rounded-xl border border-slate-700 bg-slate-800/50 p-5 shadow-sm backdrop-blur-sm min-h-[400px]">
            <h3 className="mb-6 text-lg font-bold text-slate-200">
              미국의 주요 금리 및 인플레이션 추이 (최근 10년)
            </h3>
            {/* [수정 사항] 배포 환경 대응
               - min-w-0: Flex/Grid 컨테이너 내부에서 너비 계산 오류 방지
               - style={{ width: "100%", height: 500 }}: CSS 로딩 전 인라인 스타일로 크기 강제 확보
            */}
            {/* 차트 높이를 320px -> 500px로 늘려 기울기를 더 가파르게 표현 */}
            <div className="h-[500px] w-full min-w-0" style={{ height: 500, width: "100%" }}>
              <ResponsiveContainer width="100%" height="100%">
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
                    minTickGap={100} // 연도 중복 표시 방지를 위해 간격 확대
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
                    domain={['auto', 'auto']} // 데이터 범위에 맞춰 자동 스케일링 (기울기 강조)
                  />
                  <Tooltip
                    contentStyle={{
                      backgroundColor: "#1e293b",
                      borderColor: "#475569",
                      color: "#f1f5f9",
                    }}
                    position={{ x: 0, y: 0 }} // 툴팁 위치를 상단 고정하여 그래프 가림 방지
                  />
                  <Legend wrapperStyle={{ paddingTop: "10px" }} />

                  {/* 역전 구간 강조 (Red Zone) */}
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
            <div className="mt-2 text-right text-xs text-slate-500">
              * 붉은색 영역: 장단기 금리차 역전 구간 (경제 침체 위험)
            </div>
          </div>

          {/* 해석 가이드 (하단 텍스트 박스) */}
          <div className="rounded-xl border border-slate-700 bg-white/5 p-6">
            <h3 className="mb-4 flex items-center gap-2 text-lg font-bold text-slate-200">
              <Info className="text-blue-400" size={20} />
              시장 지표 해석 가이드
            </h3>
            <div className="grid grid-cols-1 gap-6 md:grid-cols-2 text-sm text-slate-300">
              <div className="space-y-2">
                <strong className="block text-emerald-400">장단기 금리 역전</strong>
                <p className="leading-relaxed opacity-90">
                  보통은 장기 국채(10년) 금리가 단기(2년)보다 높아야 정상이지만,
                  경제 위기 우려가 커지면 단기 금리가 더 높아지는 <span className="font-bold text-red-400">'역전 현상'</span>이 발생합니다. 
                  이는 역사적으로 <span className="underline decoration-red-500/50 underline-offset-4">경기 침체의 가장 강력한 선행 지표</span>입니다.
                </p>
              </div>
              <div className="space-y-2">
                <strong className="block text-red-400">인플레이션 (CPI)</strong>
                <p className="leading-relaxed opacity-90">
                  물가가 오르면 연준이 금리를 인상하여 돈줄을 죄게 됩니다. 
                  이 과정에서 단기 금리가 급등하며 금리 역전이 발생하기 쉽습니다. 
                  즉, <span className="font-bold text-slate-100">높은 인플레이션은 경기 둔화의 방아쇠</span>가 될 수 있습니다.
                </p>
              </div>
            </div>
          </div>
        </section>

        {/* --- [Right Column] 랭킹 리스트 --- */}
        <section className="space-y-4 xl:col-span-3">
          <div className="flex items-center justify-between border-b border-slate-700 pb-2">
            <h2 className="text-lg font-bold text-slate-100">Top Ranking</h2>
            <Link to="/companies" className="text-xs text-blue-400 hover:underline">
              전체보기
            </Link>
          </div>
          
          <div className="overflow-hidden rounded-lg border border-slate-700 bg-slate-800">
            <table className="w-full text-left text-sm">
              <thead className="bg-slate-900 text-slate-400">
                <tr>
                  <th className="py-3 pl-4 font-medium">순위</th>
                  <th className="py-3 font-medium">회사</th>
                  <th className="py-3 text-center font-medium">점수</th>
                  <th className="py-3 text-center font-medium">등급</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-700/50">
                {topCompanies?.slice(0, 10).map((item, index) => (
                  <tr
                    key={item.ticker}
                    className="group hover:bg-slate-700/30 transition-colors"
                  >
                    <td className="py-3 pl-4 font-bold text-slate-500">
                      {index + 1}
                    </td>
                    <td className="py-3">
                      <Link to={`/company/${item.ticker}`} className="block">
                        <span className="block font-bold text-slate-200 group-hover:text-blue-400">
                          {item.ticker}
                        </span>
                        <span className="block max-w-[100px] truncate text-xs text-slate-500">
                          {item.name}
                        </span>
                      </Link>
                    </td>
                    <td className="py-3 text-center font-mono font-bold text-emerald-400">
                      {item.totalScore}
                    </td>
                    <td className="py-3 text-center">
                      <span
                        className={`inline-block min-w-[24px] rounded px-1.5 py-0.5 text-[10px] font-bold ${getGradeColor(
                          item.grade
                        )}`}
                      >
                        {item.grade}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>
      </div>
    </div>
  );
};

// [내부 컴포넌트] 좌측 지표 아이템
const IndicatorItem = ({
  label,
  value,
  icon,
  color,
  subText,
  isAlert = false,
}: {
  label: string;
  value: string;
  icon: React.ReactNode;
  color: string;
  subText?: string;
  isAlert?: boolean;
}) => {
  return (
    <div
      className={`group flex flex-col gap-1 rounded-lg border p-4 transition-all ${
        isAlert
          ? "border-red-500/50 bg-red-500/10"
          : "border-slate-700 bg-slate-800 hover:border-slate-600"
      }`}
    >
      <div className="flex items-center justify-between">
        <span className="text-sm font-medium text-slate-400">{label}</span>
        <div className={`opacity-70 ${color}`}>{icon}</div>
      </div>
      <div className={`text-2xl font-bold ${color}`}>{value}</div>
      {subText && <div className="text-xs text-slate-500">{subText}</div>}
    </div>
  );
};

export default HomePage;
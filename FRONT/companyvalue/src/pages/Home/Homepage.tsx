import { useQuery } from "@tanstack/react-query";
import { macroApi } from "../../api/macroApi";
import axiosClient from "../../api/axiosClient";
import { ScoreResult } from "../../types/company";
import MajorIndicators from "./components/MajorIndicators";
import TopRankingList from "./components/TopRankingList";
import MacroTrendSection from "./components/MacroTrendSection";

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
    <div className="w-full space-y-6">
      <div className="grid grid-cols-1 gap-6 xl:grid-cols-12">
        <MajorIndicators latest={latest}/>
        <MacroTrendSection latestDate={latest.date} history={history}/>
        <TopRankingList companies={topCompanies || []} />
      </div>
    </div>
  );
};


export default HomePage;
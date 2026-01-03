import { useQuery } from "@tanstack/react-query";
import { macroApi } from "../../api/macroApi";
import axiosClient from "../../api/axiosClient";
import { ScoreResult } from "../../types/company";
import MajorIndicators from "./components/MajorIndicators";
import MacroTrendSection from "./components/MacroTrendSection";
import TopRankingList from "./components/TopRankingList";
import LoadingState from "../../components/common/LoadingState";

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
    return <LoadingState message="데이터 분석 중..." />;
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
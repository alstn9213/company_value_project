import { useQuery } from "@tanstack/react-query";
import { macroApi } from "../../api/macroApi";
import axiosClient from "../../api/axiosClient";
import { ScoreResult } from "../../types/company";
import MajorIndicators from "./components/MajorIndicators";
import MacroTrendSection from "./components/MacroTrendSection";
import TopRankingList from "./components/TopRankingList";

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
  
  return (
    <div className="w-full space-y-6">
      <div className="grid grid-cols-1 gap-6 xl:grid-cols-12">
        <MajorIndicators 
          latest={latest} 
          isLoading={isLatestLoading}/>
        <MacroTrendSection 
          latestDate={latest?.date} 
          history={history} 
          isLoading={isHistoryLoading || isLatestLoading} // history는 latestDate에도 의존하므로
        />
        <TopRankingList 
          companies={topCompanies || []} 
          isLoading={isRankLoading} 
        />
      </div>
    </div>
  );
};


export default HomePage;
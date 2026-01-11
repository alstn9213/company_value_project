import MajorIndicators from "../../features/macro/components/MajorIndicators";
import MacroTrendSection from "../../features/macro/components/MacroTrendSection";
import TopRankingList from "../../features/company/components/TopRankingList";
import { useMacroHistory, useMacroLatest } from "../../features/macro/hooks/useMacroDashboard";
import { useTopRankingCompanies } from "../../features/company/hooks/useCompanyRanking";

const HomePage = () => {
  const { data: latest, isLoading: isLatestLoading } = useMacroLatest();
  const { data: history, isLoading: isHistoryLoading } = useMacroHistory();
  const { data: topCompanies, isLoading: isRankLoading } = useTopRankingCompanies();

  const isTrendLoading = isHistoryLoading || isLatestLoading;

  return (
    <div className="w-full space-y-6">
      <div className="grid grid-cols-1 gap-6 xl:grid-cols-12">
        <MajorIndicators 
          latest={latest} 
          isLoading={isLatestLoading}/>
        <MacroTrendSection 
          latestDate={latest?.date} 
          history={history} 
          isLoading={isTrendLoading} // history는 latestDate에도 의존하므로
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
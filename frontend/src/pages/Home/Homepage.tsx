import { useMacroHistory, useMacroLatest } from "../../features/macro/hooks/useMacroDashboard";
import { useTopRankingCompanies } from "../../features/company/hooks/useCompanyRanking";
import { MajorIndicatorSection } from "../../features/macro/layouts/MajorIndicatorSection";
import { EconomicChartSection } from "../../features/macro/layouts/EconomicChartSection";
import { TopRankingListSection } from "../../features/company/layouts/TopRankingListSection";

const HomePage = () => {
  const { data: latest, isLoading: isLatestLoading } = useMacroLatest();
  const { data: history, isLoading: isHistoryLoading } = useMacroHistory();
  const { data: topCompanies, isLoading: isRankLoading } = useTopRankingCompanies();

  const isTrendLoading = isHistoryLoading || isLatestLoading;

  return (
    <div className="w-full space-y-6">
      <div className="grid grid-cols-1 gap-6 xl:grid-cols-12">
        <MajorIndicatorSection 
          latest={latest} 
          isLoading={isLatestLoading}/>
        <EconomicChartSection 
          latestDate={latest?.date} 
          history={history} 
          isLoading={isTrendLoading}
        />
        <TopRankingListSection 
          companies={topCompanies || []} 
          isLoading={isRankLoading} 
        />
      </div>
    </div>
  );
};


export default HomePage;
import { MajorIndicatorSection } from "../../features/macro/layouts/MajorIndicatorSection";
import { EconomicChartSection } from "../../features/macro/layouts/EconomicChartSection";
import { TopRankingListSection } from "../../features/company/layouts/TopRankingListSection";

const HomePage = () => {
  return (
    <div className="w-full space-y-6">
      <div className="grid grid-cols-1 gap-6 xl:grid-cols-12">
        <MajorIndicatorSection />
        <EconomicChartSection />
        <TopRankingListSection />
      </div>
    </div>
  );
};


export default HomePage;
import { TopRankingListContainer } from "../../features/company";
import { EconomicChartSection, MajorIndicatorContainer,  } from "../../features/macro";

const HomePage = () => {
  return (
    <div className="w-full space-y-6">
      <div className="grid grid-cols-1 gap-6 xl:grid-cols-12">
        <MajorIndicatorContainer />
        <EconomicChartSection />
        <TopRankingListContainer />
      </div>
    </div>
  );
};


export default HomePage;
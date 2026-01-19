import { TopRankingListContainer } from "../../features/company";
import { EconomicChartContainer, MajorIndicatorContainer, } from "../../features/macro";

export const HomePage = () => {
  return (
    <div className="w-full space-y-6">
      <div className="grid grid-cols-1 gap-6 xl:grid-cols-12">
        <MajorIndicatorContainer />
        <EconomicChartContainer />
        <TopRankingListContainer />
      </div>
    </div>
  );
};


import { LineChart } from "lucide-react";
import EmptyState from "../../../components/common/EmptyState";
import { MacroData } from "../../../types/macro";
import EconomicChart from "./charts/EconomicChart";
import ChartGuide from "./guides/ChartGuide";
import MarketGuide from "./guides/MarketGuide";
import Skeleton from "../../../components/ui/Skeleton";

interface MacroTrendSectionProps {
  latestDate?: string; 
  history?: MacroData[]; 
  isLoading: boolean;
}

const MacroTrendSection = ({ latestDate, history, isLoading }: MacroTrendSectionProps) => {
  const hasData = history && history.length > 0;

  return (
    <section className="flex flex-col gap-6 xl:col-span-7">
      {/* 헤더 영역 */}
      <div className="flex items-end justify-between">
        <h2 className="text-2xl font-bold text-slate-100">미국의 경제 상황</h2>
        {/* 날짜 */}
        {isLoading ? (
          <Skeleton className="w-32 h-5 rounded bg-slate-800" />
        ) : (
          <span className="text-sm text-slate-400">기준일 {latestDate}</span>
        )}
      </div>

      {/* 메인 차트 영역 */}
      {isLoading ? (
        <Skeleton className="w-full h-80 rounded-xl bg-slate-800" />
      ) : hasData ? (
        <EconomicChart history={history} isLoading={isLoading}/>
      ) : (
        // 데이터가 없을 때 - 차트 영역 높이 유지
        <div className="w-full h-80 flex items-center justify-center rounded-xl bg-slate-800/30 border border-slate-700/50">
          <EmptyState
            icon={<LineChart size={48} />}
            title="경제 동향 데이터 없음"
            description="기간 내 조회된 거시 경제 데이터가 없습니다."
          />
        </div>
      )}

      {/* 가이드 섹션 */}
      <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
        <MarketGuide />
        <ChartGuide />
      </div>
    </section>
    );
};

export default MacroTrendSection;
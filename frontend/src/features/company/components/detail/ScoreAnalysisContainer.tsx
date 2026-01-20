import { AlertTriangle, TrendingUp } from "lucide-react";
import { RiskPenaltyAlert } from "../../ui/p_detail/RiskPenaltyAlert";
import { CompanyScoreResponse } from "../../../../types/company";
import { DetailScore } from "./DetailScore";
import { ErrorState } from "../../../../components/ui/ErrorState";
import { EmptyState } from "../../../../components/ui/EmptyState";
import { useScoreAnalytics } from "../../hooks/useScoreAnalytics";
import { ScoreAnalysisSkeleton } from "../../ui/p_detail/ScoreAnalysisSkeleton";
import { TotalScoreDisplay } from "./TotalScoreDisplay";
import { ScoreRadarChart } from "./ScoreRadarChart";

interface ScoreAnalysisProps {
  score: CompanyScoreResponse | undefined;
  isLoading: boolean;
  isError?: boolean;
  onRetry?: () => void;
}

export const ScoreAnalysisContainer =({ 
  score, 
  isLoading,
  isError,
  onRetry
}: ScoreAnalysisProps) => {
  const analytics = useScoreAnalytics(score);

  if (isLoading) {
    return <ScoreAnalysisSkeleton />;
  }

  if (isError) {
    return (
      <div className="h-full flex flex-col gap-4">
         <div className="bg-card border border-slate-700/50 rounded-xl p-6 flex-1 flex items-center justify-center shadow-lg backdrop-blur-sm min-h-[400px]">
            <ErrorState 
                title="분석 데이터를 불러올 수 없습니다" 
                onRetry={onRetry} 
            />
         </div>
      </div>
    );
  }

  if (!score || !analytics) {
    return (
      <div className="h-full flex flex-col gap-4">
        <div className="bg-card border border-slate-700/50 rounded-xl p-6 flex-1 flex items-center justify-center shadow-lg backdrop-blur-sm min-h-[400px]">
          <EmptyState 
            icon={<AlertTriangle size={48} className="text-slate-600 mb-4" />}
            title="분석 리포트 없음"
            description="해당 기업의 분석 점수 데이터가 존재하지 않습니다."
          />
        </div>
      </div>
    );
  }

  const { penaltyPoints, chartData } = analytics;
  
  return (
    <div className="h-full flex flex-col gap-4">
      {/* 페널티/위험 경고 메시지 */}
      <RiskPenaltyAlert penaltyPoints={penaltyPoints} />

      {/* 메인 분석 카드 */}
      <div className="bg-card border border-slate-700/50 rounded-xl p-6 flex-1 flex flex-col shadow-lg backdrop-blur-sm">

        <h3 className="text-lg font-bold text-white mb-6 flex items-center gap-2">
          <TrendingUp size={20} className="text-emerald-400" />
          분석 리포트
        </h3>

        {/* 종합 점수 표시 */}
        <TotalScoreDisplay totalScore={score.totalScore} />

        {/* 레이더 차트 영역 */}
        <div className="flex-1 min-h-[200px] flex items-center justify-center -ml-4">
          <ScoreRadarChart data={chartData} />
        </div>

        {/* 세부 점수 리스트 */}
        <DetailScore score={score} />
      </div>
    </div>
  );
};


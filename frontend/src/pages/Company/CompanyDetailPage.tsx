import { useParams } from "react-router-dom";
import { useCompanyDetail } from "../../features/company/hooks/useCompanyDetail";
import { ScoreAnalysisSection } from "../../features/valuation/layouts/ScoreAnalysisSection";
import { CompanyHeader } from "../../features/company/components/p_detail/CompanyHeader";
import { ErrorState } from "../../components/ui/ErrorState";
import { StockChartSection } from "../../features/company/layouts/StockChartSection";
import { FinancialSummary } from "../../features/company/layouts/FinancialSummary";

const CompanyDetailPage = () => {
  const { ticker } = useParams<{ ticker: string }>();
  const { data, isLoading, isError, refetch } = useCompanyDetail(ticker);

  if (!ticker) {
      return null;
  }

  if (!data) {
    return null;
  }

  if (isError) {
    return (
      <ErrorState 
        title="기업 정보를 찾을 수 없습니다"
        message="데이터를 불러오는 데 실패했습니다. 네트워크 상태를 확인하거나 잠시 후 다시 시도해주세요."
        onRetry={() => refetch()} 
      />
    );
  }

  const {companySummary, score, latestFinancial} = data;

  return (
    <div className="max-w-6xl mx-auto space-y-6 pb-10">
      {/* 헤더 */}
      <CompanyHeader 
        info={companySummary} 
        score={score}
        isLoading={isLoading}
        />

      {/* 기업 정보 대시 보드 */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* 좌측: 분석 점수 */}
        <div className="lg:col-span-1 space-y-6">
          <ScoreAnalysisSection 
            score={score} 
            isLoading={isLoading}
            />
        </div>
        {/* 우측: 차트 및 재무제표 */}
        <div className="lg:col-span-2 space-y-12">
          <StockChartSection ticker={ticker} />
          <FinancialSummary 
            financial={latestFinancial} 
            isLoading={isLoading}
            />
        </div>
      </div>
    </div>
  );
};

export default CompanyDetailPage;

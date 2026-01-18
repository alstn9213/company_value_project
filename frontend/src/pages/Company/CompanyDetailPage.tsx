import { useParams } from "react-router-dom";
import { ScoreAnalysisSection } from "../../features/valuation/layouts/ScoreAnalysisSection";
import { CompanyHeader } from "../../features/company/components/p_detail/CompanyHeader";
import { ErrorState } from "../../components/ui/ErrorState";
import { 
  useCompanyDetail, 
  StockChartSection, 
  FinancialSummary 
} from '../../features/company';

const CompanyDetailPage = () => {
  const { ticker } = useParams<{ ticker: string }>();
  const { summary, score, financial, isLoading, isError, refetch } = useCompanyDetail(ticker);

  if (!ticker) {
      return null;
  }

  if (isError) {
    return (
      <ErrorState 
        title="기업 정보를 찾을 수 없습니다"
        onRetry={refetch}
      />
    );
  }

  return (
    <div className="max-w-6xl mx-auto space-y-6 pb-10">
      {/* 헤더 */}
      <CompanyHeader 
        info={summary} 
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
            financial={financial} 
            isLoading={isLoading}
            />
        </div>
      </div>
    </div>
  );
};

export default CompanyDetailPage;

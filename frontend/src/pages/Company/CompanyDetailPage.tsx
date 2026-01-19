import { useParams } from "react-router-dom";
import { 
  useCompanyDetail, 
  FinancialSummary, 
  StockChartContainer,
  CompanyHeader,
  ScoreAnalysisContainer,
} from '../../features/company';

export const CompanyDetailPage = () => {
  const { ticker } = useParams<{ ticker: string }>();
  const { summary, score, financial, isLoading, isError, refetch } = useCompanyDetail(ticker);

  if (!ticker) {
      return null;
  }

  return (
    <div className="max-w-6xl mx-auto space-y-6 pb-10">
      {/* 헤더 */}
      <CompanyHeader 
        info={summary} 
        score={score}
        isLoading={isLoading}
        isError={isError}
        onRetry={refetch}
        />
      {/* 기업 정보 대시 보드 */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* 좌측: 분석 점수 */}
        <div className="lg:col-span-1 space-y-6">
          <ScoreAnalysisContainer
            score={score} 
            isLoading={isLoading}
            isError={isError}
            onRetry={refetch}
            />
        </div>
        {/* 우측: 차트 및 재무제표 */}
        <div className="lg:col-span-2 space-y-12">
          <StockChartContainer 
            ticker={ticker} 
          />
          <FinancialSummary 
            financial={financial} 
            isLoading={isLoading}
            isError={isError}
            onRetry={refetch}
          />
        </div>
      </div>
    </div>
  );
};


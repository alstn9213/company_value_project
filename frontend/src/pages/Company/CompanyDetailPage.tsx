import { useNavigate, useParams } from "react-router-dom";
import { ArrowLeft } from "lucide-react";
import { useCompanyDetail } from "../../features/company/hooks/useCompanyDetail";
import { ScoreAnalysisSection } from "../../features/valuation/layouts/ScoreAnalysisSection";
import { CompanyHeader } from "../../features/company/components/CompanyHeader";
import { StockChartSection } from "../../features/company/components/StockChartSection";
import { FinancialSummary } from "../../features/company/components/FinancialSummary";
import { ErrorState } from "../../components/common/ErrorState";
import { LoadingState } from "../../components/ui/LoadingState";

const CompanyDetailPage = () => {
  const {ticker} = useParams<{ ticker: string }>();
  const navigate = useNavigate();

  const { data, isLoading, isError, refetch } = useCompanyDetail(ticker);

  if (isLoading) {
    return <LoadingState message="기업 상세 정보를 분석 중입니다..." />;
  }
  
  if (isError || !data) {
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
      <button
        onClick={() => navigate(-1)}
        className="flex items-center gap-2 text-slate-400 hover:text-white transition-colors mb-2"
      >
        <ArrowLeft size={18} /> 목록으로 돌아가기
      </button>

      <CompanyHeader info={companySummary} score={score}/>

      {/* 대시보드 그리드 */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* 좌측: 분석 점수 */}
        <div className="lg:col-span-1 space-y-6">
          <ScoreAnalysisSection score={score} />
        </div>
        {/* 우측: 차트 및 재무제표 */}
        <div className="lg:col-span-2 space-y-12">
          <StockChartSection ticker={ticker!} />
          <FinancialSummary financial={latestFinancial} />
        </div>
      </div>
    </div>
  );
};

export default CompanyDetailPage;

import { useQuery } from "@tanstack/react-query";
import { useNavigate, useParams } from "react-router-dom";
import { companyApi } from "../../api/companyApi";
import { ArrowLeft } from "lucide-react";
import CompanyHeader from "./components/CompanyHeader";
import ScoreAnalysis from "./components/ScoreAnalysis";
import StockChartSection from "./components/StockChartSection";
import FinancialSummary from "./components/FinancialSummary";

const CompanyDetailPage = () => {
  const {ticker} = useParams<{ ticker: string }>();
  const navigate = useNavigate();

  const {data, isLoading, isError} = useQuery({
    queryKey: ["company", ticker],
    queryFn: () => companyApi.getDetail(ticker!), // ticker가 null이나 undefined가 아님을 명시적으로 알리는 역할: !
    enabled: !!ticker,
  });

  if (isLoading) return <div className="...">로딩 중...</div>;
  if (isError || !data) return <div className="...">에러...</div>;

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
          <ScoreAnalysis score={score} />
        </div>
        {/* 우측: 차트 및 재무제표 */}
        <div className="lg:col-span-2 space-y-6">
          <StockChartSection ticker={ticker!} />
          <FinancialSummary financial={latestFinancial} />
        </div>
      </div>
    </div>
  );
};

export default CompanyDetailPage;

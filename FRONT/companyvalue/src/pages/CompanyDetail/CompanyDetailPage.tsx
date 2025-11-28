import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useNavigate, useParams } from "react-router-dom";
import { companyApi } from "../../api/companyApi";
import {
  formatCurrency,
  getGradeColor,
  getScoreColor,
} from "../../utils/formatters";
import {
  AlertTriangle,
  ArrowLeft,
  Building2,
  Sparkles,
  Star,
  TrendingUp,
} from "lucide-react";
import ScoreRadarChart from "../../components/charts/ScoreRadarChart";
import { watchlistApi } from "../../api/watchlistApi";
import { useAuthStore } from "../../stores/authStore";
import { AxiosError } from "axios";
import FinancialTrendChart from "../../components/charts/FinancialTrendChart";

const CompanyDetailPage = () => {
  const { ticker } = useParams<{ ticker: string }>();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { isAuthenticated } = useAuthStore();
  const { data, isLoading, isError } = useQuery({
    queryKey: ["company", ticker],
    queryFn: () => companyApi.getDetail(ticker!),
    enabled: !!ticker,
  });

  // 관심 종목 추가 핸들러
  const handleAddWatchlist = () => {
    if (!isAuthenticated) {
      if (
        confirm("로그인이 필요한 기능입니다. 로그인 페이지로 이동하시겠습니까?")
      ) {
        navigate("/login");
      }
      return;
    }
    addWatchlistMutation.mutate(ticker!);
  };

  // 관심 종목 추가 Mutation
  const addWatchlistMutation = useMutation({
    mutationFn: (ticker: string) => watchlistApi.add(ticker),
    onSuccess: () => {
      alert("관심 종목에 추가되었습니다.");
      queryClient.invalidateQueries({ queryKey: ["watchlist"] });
    },
    onError: (error: AxiosError) => {
      // 백엔드에서 중복 시 400 Bad Request를 보냄
      if (error.response?.status === 400) {
        alert("이미 관심 목록에 존재하는 기업입니다.");
      } else {
        alert("추가 중 오류가 발생했습니다.");
      }
    },
  });

  if (isLoading)
    return (
      <div className="text-center p-20 text-slate-400">데이터 분석 중...</div>
    );
  if (isError || !data)
    return (
      <div className="text-center p-20 text-red-400">
        기업 정보를 찾을 수 없습니다.
      </div>
    );

  const { info, score, latestFinancial: fin } = data;

  // 점수가 아직 계산되지 않은 경우(N/A) 처리
  if (score.grade === "N/A") {
    return (
      <div className="max-w-6xl mx-auto py-20 text-center space-y-4">
        <h2 className="text-3xl font-bold text-white">
          {info.name} ({info.ticker})
        </h2>
        <div className="bg-slate-800/50 p-8 rounded-xl border border-slate-700 inline-block">
          <p className="text-xl text-yellow-400 font-bold mb-2">
            ⚠ 분석 데이터 대기 중
          </p>
          <p className="text-slate-400">
            현재 해당 기업의 재무 데이터를 분석하고 있습니다.
            <br />
            잠시 후 다시 시도해주세요.
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-6xl mx-auto space-y-6 pb-10">
      {/* 상단 네비게이션 */}
      <button
        onClick={() => navigate(-1)}
        className="flex items-center gap-2 text-slate-400 hover:text-white transition-colors mb-2"
      >
        <ArrowLeft size={18} /> 목록으로 돌아가기
      </button>

      {/* 1. 헤더 카드 (기업 기본 정보 + 등급 뱃지 + 관심종목 버튼) */}
      <div className="bg-card border border-slate-700/50 rounded-2xl p-8 shadow-lg backdrop-blur-sm flex flex-col md:flex-row justify-between items-center gap-6">
        <div className="flex items-center gap-6">
          {/* ... 로고 및 기업 이름 ... */}
          <div className="w-16 h-16 rounded-2xl bg-slate-800 flex items-center justify-center text-2xl font-bold text-slate-200 shadow-inner">
            {info.ticker[0]}
          </div>
          <div>
            <div className="flex items-center gap-3">
              <h1 className="text-3xl font-bold text-white">{info.name}</h1>
              <span className="text-sm text-slate-400 bg-slate-800 px-2 py-1 rounded">
                {info.ticker}
              </span>
            </div>
            <div className="flex items-center gap-4 mt-2 text-slate-400 text-sm">
              <span className="flex items-center gap-1">
                <Building2 size={14} /> {info.exchange}
              </span>
              <span>|</span>
              <span>{info.sector}</span>
            </div>
          </div>
        </div>

        <div className="flex items-center gap-6">
          {/* 관심 종목 추가 버튼 */}
          <button
            onClick={handleAddWatchlist}
            className="flex flex-col items-center gap-1 text-slate-400 hover:text-yellow-400 transition-colors group"
            title="관심 종목 추가"
          >
            <div className="p-3 rounded-full bg-slate-800 group-hover:bg-yellow-400/10 border border-slate-600 group-hover:border-yellow-400/50 transition-all shadow-md">
              <Star
                size={24}
                className="group-hover:fill-yellow-400 transition-colors"
              />
            </div>
            <span className="text-xs font-medium group-hover:text-yellow-400">
              관심등록
            </span>
          </button>

          {/* 종합 등급 표시 */}
          <div className="flex flex-col items-center">
            <span className="text-slate-400 text-sm mb-1">투자 적합 등급</span>
            <div
              className={`w-20 h-20 rounded-full border-4 flex items-center justify-center text-4xl font-bold shadow-[0_0_20px_rgba(0,0,0,0.3)] ${getGradeColor(
                score.grade
              )}`}
            >
              {score.grade}
            </div>
            {/* 저점 매수 뱃지 추가 */}
            {score.isOpportunity && (
              <div className="mt-3 flex items-center gap-1 px-3 py-1 rounded-full bg-blue-500/20 border border-blue-400 text-blue-300 text-xs font-bold animate-pulse shadow-[0_0_10px_rgba(59,130,246,0.5)]">
                <Sparkles size={12} className="text-blue-300 fill-blue-300" />
                <span>저점 매수 기회</span>
              </div>
            )}
          </div>
        </div>
      </div>


      {/* 2. 분석 대시보드 그리드 */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* 좌측: 종합 점수 및 차트 */}
        <div className="lg:col-span-1 space-y-6">
          {/* F등급일 때 안내 메시지 추가 */}
            {(score.grade.includes("F") || score.totalScore === 0) && (
              <div className="mt-4 p-3 bg-red-500/10 border border-red-500/30 rounded-lg text-sm text-red-200">
                <p className="font-bold flex items-center gap-2">
                  <AlertTriangle size={16} />
                  투자 위험 경고
                </p>
                <p className="mt-1 opacity-80">
                  이 기업은 <strong>자본 잠식</strong> 상태이거나{" "}
                  <strong>부채 비율이 과도(400% 초과)</strong>하여 평가 대상에서
                  제외(0점 처리)되었습니다.
                </p>
              </div>
            )}
          <div className="bg-card border border-slate-700/50 rounded-xl p-6 h-full">
            <h3 className="text-lg font-bold text-white mb-4 flex items-center gap-2">
              <TrendingUp size={20} className="text-emerald-400" /> 분석 리포트
            </h3>

            <div className="text-center mb-6">
              <span className="text-slate-400 text-sm">종합 점수</span>
              <div
                className={`text-5xl font-bold mt-1 ${getScoreColor(
                  score.totalScore
                )}`}
              >
                {score.totalScore}
                <span className="text-xl text-slate-500">/100</span>
              </div>
            </div>

            {/* 레이더 차트 */}
            <ScoreRadarChart score={score} />

            {/* 세부 점수 테이블 */}
            <div className="mt-6 space-y-3">
              <ScoreRow
                label="안정성 (부채/유동성)"
                value={score.stabilityScore}
                max={40}
              />
              <ScoreRow
                label="수익성 (ROE/마진)"
                value={score.profitabilityScore}
                max={30}
              />
              <ScoreRow
                label="내재가치 (PER/PBR)"
                value={score.valuationScore}
                max={20}
              />
              <ScoreRow
                label="미래투자 (R&D)"
                value={score.investmentScore}
                max={10}
              />
            </div>
          </div>
        </div>

        {/* 우측: 핵심 재무 데이터 + 차트 */}
        <div className="lg:col-span-2 space-y-6">
          {/* 실적 추이 차트 추가 */}
          {data.financialHistory && data.financialHistory.length > 0 && (
            <FinancialTrendChart data={data.financialHistory} />
          )}
          <div className="bg-card border border-slate-700/50 rounded-xl p-6 h-fit">
            <div className="flex justify-between items-end mb-6">
              <h3 className="text-lg font-bold text-white">
                📊 최신 재무제표 ({fin.year} Q{fin.quarter})
              </h3>
              <span className="text-xs text-slate-500">* 단위: USD</span>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <FinancialCard
                label="매출액 (Revenue)"
                value={fin.revenue}
                color="text-white"
              />
              <FinancialCard
                label="영업이익 (Op. Profit)"
                value={fin.operatingProfit}
                color="text-blue-300"
              />
              <FinancialCard
                label="당기순이익 (Net Income)"
                value={fin.netIncome}
                color="text-emerald-300"
              />
              <FinancialCard
                label="영업현금흐름 (OCF)"
                value={fin.operatingCashFlow}
                color="text-yellow-300"
              />

              <div className="border-t border-slate-700 col-span-1 sm:col-span-2 my-2"></div>

              <FinancialCard
                label="자산 총계 (Assets)"
                value={fin.totalAssets}
              />
              <FinancialCard
                label="부채 총계 (Liabilities)"
                value={fin.totalLiabilities}
              />
              <FinancialCard
                label="자본 총계 (Equity)"
                value={fin.totalEquity}
              />
              <FinancialCard
                label="R&D 투자비용"
                value={fin.researchAndDevelopment}
                color="text-purple-300"
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

// 내부 컴포넌트: 점수 행
const ScoreRow = ({
  label,
  value,
  max,
}: {
  label: string;
  value: number;
  max: number;
}) => (
  <div className="flex justify-between items-center text-sm">
    <span className="text-slate-400">{label}</span>
    <div className="flex items-center gap-2">
      <div className="w-24 h-2 bg-slate-700 rounded-full overflow-hidden">
        <div
          className="h-full bg-blue-500 rounded-full"
          style={{ width: `${(value / max) * 100}%` }}
        />
      </div>
      <span className="font-mono text-slate-200 w-8 text-right">{value}</span>
    </div>
  </div>
);

// 내부 컴포넌트: 재무 카드
const FinancialCard = ({
  label,
  value,
  color = "text-slate-200",
}: {
  label: string;
  value: number;
  color?: string;
}) => (
  <div className="bg-slate-800/50 p-4 rounded-lg flex justify-between items-center">
    <span className="text-slate-400 text-sm">{label}</span>
    <span className={`font-mono font-semibold ${color}`}>
      {formatCurrency(value)}
    </span>
  </div>
);

export default CompanyDetailPage;

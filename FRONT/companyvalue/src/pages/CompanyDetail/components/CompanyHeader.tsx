import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";
import { useAuthStore } from "../../../stores/authStore";
import { watchlistApi } from "../../../api/watchlistApi";
import { AxiosError } from "axios";
import { Building2, Sparkles, Star } from "lucide-react";
import { getGradeColor } from "../../../utils/formatters";

interface CompanyInfo {
  ticker: string;
  name: string;
  exchange: string;
  sector: string;
}

interface CompanyScore {
  grade: string;
  isOpportunity: boolean;
}

interface Props {
  info: CompanyInfo;
  score: CompanyScore;
}

const CompanyHeader = ({info, score}: Props) => {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const {isAuthenticated} = useAuthStore();

  const addWatchlistMutation = useMutation({
    mutationFn: (ticker: string) => watchlistApi.add(ticker),
    onSuccess: () => {
      alert("관심 종목에 추가되었습니다.");
      queryClient.invalidateQueries({queryKey: ["watchlist"]});
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

  const handleAddWatchlist = () => {
    if (!isAuthenticated) {
      if (confirm("로그인이 필요한 기능입니다. 로그인 페이지로 이동하시겠습니까?")) {
        navigate("/login");
      }
      return;
    }
    addWatchlistMutation.mutate(info.ticker);
  };

  return (
    <div className="bg-card border border-slate-700/50 rounded-2xl p-8 shadow-lg backdrop-blur-sm flex flex-col md:flex-row justify-between items-center gap-6">
      {/* 1. 좌측: 기업 로고 및 기본 정보 */}
      <div className="flex items-center gap-6">
        {/* 로고 (티커 앞글자) */}
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

      {/* 2. 우측: 액션 버튼 및 등급 배지 */}
      <div className="flex items-center gap-6">
        
        {/* 관심 종목 추가 버튼 */}
        <button
          onClick={handleAddWatchlist}
          disabled={addWatchlistMutation.isPending}
          className="flex flex-col items-center gap-1 text-slate-400 hover:text-yellow-400 transition-colors group"
          title="관심 종목 추가"
        >
          <div className="p-3 rounded-full bg-slate-800 group-hover:bg-yellow-400/10 border border-slate-600 group-hover:border-yellow-400/50 transition-all shadow-md">
            <Star
              size={24}
              className={`group-hover:fill-yellow-400 transition-colors ${
                addWatchlistMutation.isPending ? "opacity-50" : ""
              }`}
            />
          </div>
          <span className="text-xs font-medium group-hover:text-yellow-400">
            관심등록
          </span>
        </button>

        {/* 투자 등급 표시 */}
        <div className="flex flex-col items-center">
          <span className="text-slate-400 text-sm mb-1">투자 적합 등급</span>
          <div
            className={`w-20 h-20 rounded-full border-4 flex items-center justify-center text-4xl font-bold shadow-[0_0_20px_rgba(0,0,0,0.3)] ${getGradeColor(
              score.grade
            )}`}
          >
            {score.grade}
          </div>
          
          {/* 저점 매수 기회 뱃지 */}
          {score.isOpportunity && (
            <div className="mt-3 flex items-center gap-1 px-3 py-1 rounded-full bg-blue-500/20 border border-blue-400 text-blue-300 text-xs font-bold animate-pulse shadow-[0_0_10px_rgba(59,130,246,0.5)]">
              <Sparkles size={12} className="text-blue-300 fill-blue-300" />
              <span>저점 매수 기회</span>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default CompanyHeader;
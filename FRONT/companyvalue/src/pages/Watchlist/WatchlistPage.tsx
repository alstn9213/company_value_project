import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { watchlistApi } from "../../api/watchlistApi";
import { AlertCircle, Trash2, TrendingUp } from "lucide-react";
import { Link } from "react-router-dom";
import { getGradeColor, getScoreColor } from "../../utils/formatters";

const WatchlistPage = () => {
  const queryClient = useQueryClient();

  const { data: watchlist, isLoading } = useQuery({
    queryKey: ["watchlist"],
    queryFn: watchlistApi.getMyWatchlist,
  });

  const deleteMutation = useMutation({
    mutationFn: watchlistApi.remove,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["watchlist"] });
    },
    onError: (error) => {
      console.error("삭제 실패:", error);
      alert("삭제 중 오류가 발생했습니다.");
    },
  });

  const handleDelete = (e: React.MouseEvent, id: number) => {
    e.preventDefault();
    if (confirm("관심 목록에서 삭제하시겠습니까?")) {
      deleteMutation.mutate(id);
    }
  };

  if (isLoading) {
    return (
      <div className="text-center p-20 text-slate-400">불러오는 중...</div>
    );
  }

  // 데이터가 없을 경우
  if (!watchlist || watchlist.length === 0) {
    return (
      <div className="max-w-7xl mx-auto text-center py-20">
        <div className="bg-slate-800/30 rounded-xl border border-slate-700/50 p-10 inline-block">
          <AlertCircle className="w-12 h-12 text-slate-500 mx-auto mb-4" />
          <h2 className="text-xl font-bold text-slate-300 mb-2">
            관심 종목이 비어있습니다.
          </h2>
          <p className="text-slate-500 mb-6">
            기업 목록에서 마음에 드는 기업을 추가해보세요.
          </p>
          <Link
            to="/companies"
            className="px-6 py-2 bg-blue-600 hover:bg-blue-500 text-white rounded-lg transition-colors"
          >
            기업 찾으러 가기
          </Link>
        </div>
      </div>
    );
  }
  return (
    <div className="max-w-7xl mx-auto space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-yellow-400 to-orange-400">
          My Watchlist
        </h1>
        <p className="text-slate-400 mt-2">
          내가 찜한 기업들의 최신 평가 등급을 확인하세요.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        {watchlist.map((item) => (
          <Link
            key={item.watchlistId}
            to={`/company/${item.ticker}`}
            className="group relative bg-card border border-slate-700/50 rounded-xl p-6 hover:border-blue-500/50 transition-all duration-300 hover:-translate-y-1 hover:shadow-lg block"
          >
            <div className="flex justify-between items-start mb-4">
              <div>
                <span className="inline-block px-2 py-0.5 rounded text-xs font-bold bg-slate-800 text-slate-400 mb-2">
                  {item.ticker}
                </span>
                <h3 className="text-xl font-bold text-white truncate pr-4">
                  {item.name}
                </h3>
              </div>
              
              {/* 등급 뱃지 */}
              <div
                className={`w-10 h-10 rounded-lg border-2 flex items-center justify-center text-lg font-bold ${getGradeColor(
                  item.currentGrade
                )}`}
              >
                {item.currentGrade}
              </div>
            </div>

            <div className="flex items-center justify-between mt-4 pt-4 border-t border-slate-700/50">
              <div className="flex items-center gap-2">
                <TrendingUp size={16} className="text-slate-500" />
                <span className={`font-bold ${getScoreColor(item.currentScore)}`}>
                  {item.currentScore}점
                </span>
              </div>

              {/* 삭제 버튼 */}
              <button
                onClick={(e) => handleDelete(e, item.watchlistId)}
                className="p-2 text-slate-500 hover:text-red-400 hover:bg-red-400/10 rounded-full transition-colors z-20"
                title="목록에서 삭제"
              >
                <Trash2 size={18} />
              </button>
            </div>
          </Link>
        ))}
      </div>
    </div>
  );
};

export default WatchlistPage;

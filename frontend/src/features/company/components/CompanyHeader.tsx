import { Company, ScoreResult } from "../../../types/company";
import { WatchlistButton } from "../../watchlist/components/WatchlistButton";
import { useAddWatchlist } from "../../watchlist/hooks/useAddWatchlist";
import { CompanyProfile } from "../layouts/CompanyProfile";
import { InvestmentGradeBadge } from "../layouts/InvestmentGradeBadge";

interface Props {
  info: Company;
  score: ScoreResult;
}

const CompanyHeader = ({ info, score }: Props) => {
  const { addWatchlist, isPending } = useAddWatchlist();

 return (
    <div className="bg-card border border-slate-700/50 rounded-2xl p-8 shadow-lg backdrop-blur-sm flex flex-col md:flex-row justify-between items-center gap-6">
      {/* 좌측: 기업 프로필 */}
      <CompanyProfile
        ticker={info.ticker}
        name={info.name}
        exchange={info.exchange}
        sector={info.sector}
      />

      {/* 우측: 관심 목록 등록 버튼 및 등급 */}
      <div className="flex items-center gap-6">
        <WatchlistButton
          onClick={() => addWatchlist(info.ticker)}
          isPending={isPending}
        />
        <InvestmentGradeBadge
          grade={score.grade}
          isOpportunity={score.isOpportunity}
        />
      </div>
    </div>
  );
};

export default CompanyHeader;
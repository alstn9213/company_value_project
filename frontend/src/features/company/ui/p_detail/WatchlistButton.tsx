import { Star } from "lucide-react";
import { cn } from "../../../../utils/cn";

interface Props {
  onClick: () => void;
  isPending: boolean;
  isWatchlisted: boolean;
}

export const WatchlistButton = ({ onClick, isPending, isWatchlisted }: Props) => {
  const buttonText = isWatchlisted ? "관심종목" : "관심등록";
  const buttonTitle = isWatchlisted ? "관심 종목에서 제거" : "관심 종목 추가";

  return (
    <button
      onClick={onClick}
      disabled={isPending}
      className={cn(
        "flex flex-col items-center gap-1 text-slate-400 hover:text-yellow-400 transition-colors group",
        isWatchlisted && "text-yellow-400"
      )}
      title={buttonTitle}
    >
      <div
        className={cn(
          "p-3 rounded-full bg-slate-800 group-hover:bg-yellow-400/10 border border-slate-600 group-hover:border-yellow-400/50 transition-all shadow-md",
          isWatchlisted && "bg-yellow-400/10 border-yellow-400/50"
        )}
      >
        <Star
          size={24}
          className={cn(
            "group-hover:fill-yellow-400 transition-colors",
            isWatchlisted && "fill-yellow-400",
            isPending && "opacity-50"
          )}
        />
      </div>

      <span className="text-xs font-medium group-hover:text-yellow-400">
        {buttonText}
      </span>
    </button>
  );
};
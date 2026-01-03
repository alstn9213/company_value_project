import { Star } from "lucide-react";

interface Props {
  onClick: () => void;
  isPending: boolean;
}

export const WatchlistButton = ({ onClick, isPending }: Props) => {
  return (
    <button
      onClick={onClick}
      disabled={isPending}
      className="flex flex-col items-center gap-1 text-slate-400 hover:text-yellow-400 transition-colors group"
      title="관심 종목 추가"
    >
      <div className="p-3 rounded-full bg-slate-800 group-hover:bg-yellow-400/10 border border-slate-600 group-hover:border-yellow-400/50 transition-all shadow-md">
        <Star
          size={24}
          className={`group-hover:fill-yellow-400 transition-colors ${
            isPending ? "opacity-50" : ""
          }`}
        />
      </div>
      <span className="text-xs font-medium group-hover:text-yellow-400">
        관심등록
      </span>
    </button>
  );
};
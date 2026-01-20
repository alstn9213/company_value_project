import { Link } from "react-router-dom";
import { TrendingUp } from "lucide-react";

export const Logo = () => {
  return (
    <Link to="/" className="flex items-center gap-2 group shrink-0">
      <div className="rounded-lg bg-emerald-500/20 p-2 text-emerald-400 transition-colors group-hover:bg-emerald-500/30">
        <TrendingUp size={24} />
      </div>
      <span className="text-xl font-bold tracking-wide text-white">
        VALUE PICK
      </span>
    </Link>
  );
};

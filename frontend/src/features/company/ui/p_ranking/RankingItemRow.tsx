import { Link } from "react-router-dom";
import { CompanyScoreResponse } from "../../../../types/company";
import { getGradeColor } from "../../../../utils/formatters";

interface RankingItemRowProps {
  data: CompanyScoreResponse;
  rank: number;
}

export const RankingItemRow = ({ data, rank }: RankingItemRowProps) => (
  <tr className="group hover:bg-slate-700/30 transition-colors">

    <td className="py-3 pl-4 font-bold text-slate-500">{rank}</td>

    <td className="py-3">
      <Link to={`/company/${data.ticker}`} className="block">
        <span className="block font-bold text-slate-200 group-hover:text-blue-400">
          {data.ticker}
        </span>
        <span className="block max-w-[100px] truncate text-xs text-slate-500">
          {data.name}
        </span>
      </Link>
    </td>

    <td className="py-3 text-center font-mono font-bold text-emerald-400">
      {data.totalScore}
    </td>

    <td className="py-3 text-center">
      <span className={`inline-block min-w-6 rounded px-1.5 py-0.5 text-[10px] font-bold ${getGradeColor(data.grade)}`}>
        {data.grade}
      </span>
    </td>
    
  </tr>
);
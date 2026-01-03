import { Link } from "react-router-dom";
import { ScoreResult } from "../../../types/company";
import { getGradeColor } from "../../../utils/formatters";

interface TopRankingListProps {
  companies: ScoreResult[];
}

const TopRankingList = ({companies}: TopRankingListProps) => {
    return(
          <section className="space-y-4 xl:col-span-3">
          <div className="flex items-center justify-between border-b border-slate-700 pb-2">
            <h2 className="text-lg font-bold text-slate-100">Top Ranking</h2>
            <Link to="/companies" className="text-xs text-blue-400 hover:underline">
              전체보기
            </Link>
          </div>
          
          <div className="overflow-hidden rounded-lg border border-slate-700 bg-slate-800">
            <table className="w-full text-left text-sm">
              <thead className="bg-slate-900 text-slate-400">
                <tr>
                  <th className="py-3 pl-4 font-medium">순위</th>
                  <th className="py-3 font-medium">회사</th>
                  <th className="py-3 text-center font-medium">점수</th>
                  <th className="py-3 text-center font-medium">등급</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-700/50">
                {companies?.slice(0, 10).map((item, index) => (
                  <tr
                    key={item.ticker}
                    className="group hover:bg-slate-700/30 transition-colors"
                  >
                    <td className="py-3 pl-4 font-bold text-slate-500">
                      {index + 1}
                    </td>
                    <td className="py-3">
                      <Link to={`/company/${item.ticker}`} className="block">
                        <span className="block font-bold text-slate-200 group-hover:text-blue-400">
                          {item.ticker}
                        </span>
                        <span className="block max-w-[100px] truncate text-xs text-slate-500">
                          {item.name}
                        </span>
                      </Link>
                    </td>
                    <td className="py-3 text-center font-mono font-bold text-emerald-400">
                      {item.totalScore}
                    </td>
                    <td className="py-3 text-center">
                      <span
                        className={`inline-block min-w-[24px] rounded px-1.5 py-0.5 text-[10px] font-bold ${getGradeColor(
                          item.grade
                        )}`}
                      >
                        {item.grade}
                      </span>
                    </td>
                  </tr>
                ))}
                    {companies.length === 0 && (
                <tr>
                    <td colSpan={4} className="py-8 text-center text-slate-500">
                    데이터가 없습니다.
                    </td>
                </tr>
                )}
              </tbody>
            </table>
          </div>
        </section>
    )
}

export default TopRankingList;
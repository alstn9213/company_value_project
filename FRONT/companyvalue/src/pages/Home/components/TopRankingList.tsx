import { Link } from "react-router-dom";
import { ScoreResult } from "../../../types/company";
import { getGradeColor } from "../../../utils/formatters";
import EmptyState from "../../../components/common/EmptyState";
import { ClipboardList } from "lucide-react";
import Skeleton from "../../../components/common/Skeleton";

interface TopRankingListProps {
  companies: ScoreResult[];
  isLoading: boolean;
}

const TopRankingList = ({companies, isLoading}: TopRankingListProps) => {
    return(
      <section className="space-y-4 xl:col-span-3">
      {/* 헤더 영역 */}
      <div className="flex items-center justify-between border-b border-slate-700 pb-2">
        <h2 className="text-lg font-bold text-slate-100">Top Ranking</h2>
        <Link to="/companies" className="text-xs text-blue-400 hover:underline">전체보기</Link>
      </div>
      
      {/* 테이블 영역 */}
      <div className="overflow-hidden rounded-lg border border-slate-700 bg-slate-800">
        <table className="w-full text-left text-sm">
          <thead className="bg-slate-900 text-slate-400">
            <tr>
              <th className="py-3 pl-4 font-medium w-12">순위</th>
              <th className="py-3 font-medium">회사</th>
              <th className="py-3 text-center font-medium w-16">점수</th>
              <th className="py-3 text-center font-medium w-16">등급</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-700/50">
            {isLoading ? (
              // Case 1: 로딩 중 (Skeleton Rows)
              // 테이블 구조를 깨지 않기 위해 tr > td 안에 스켈레톤을 넣습니다.
              [1, 2, 3, 4, 5, 6, 7, 8, 9, 10].map((i) => (
                <tr key={i}>
                  <td className="py-3 pl-4">
                    <Skeleton className="h-4 w-4 rounded bg-slate-700" />
                  </td>
                  <td className="py-3">
                    <div className="space-y-1">
                      <Skeleton className="h-4 w-12 rounded bg-slate-700" />
                      <Skeleton className="h-3 w-20 rounded bg-slate-700" />
                    </div>
                  </td>
                  <td className="py-3 text-center">
                    <Skeleton className="mx-auto h-4 w-8 rounded bg-slate-700" />
                  </td>
                  <td className="py-3 text-center">
                    <Skeleton className="mx-auto h-5 w-8 rounded bg-slate-700" />
                  </td>
                </tr>
              ))
            ) : companies.length > 0 ? (
              // Case 2: 데이터 존재 시 (실제 렌더링)
              companies.slice(0, 10).map((item, index) => (
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
              ))
            ) : (
              // Case 3: 데이터 없음 (EmptyState 재사용)
              <tr>
                <td colSpan={4} className="py-10">
                  <EmptyState
                    icon={<ClipboardList size={40} />}
                    title="랭킹 데이터가 없습니다."
                    description="집계된 우량주 데이터가 아직 없습니다."
                  />
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </section>
  );
}

export default TopRankingList;
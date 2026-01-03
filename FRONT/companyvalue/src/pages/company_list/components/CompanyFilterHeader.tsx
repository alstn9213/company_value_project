import { Filter, Search } from "lucide-react";

interface CompanyFilterHeaderProps {
  searchTerm: string;
  onSearchChange: (value: string) => void;
  sortOption: string;
  onSortChange: (value: string) => void;
  isSearchActive: boolean; // 검색 중일 때 정렬 비활성화 등을 위해
}

const CompanyFilterHeader = ({
  searchTerm,
  onSearchChange,
  sortOption,
  onSortChange,
  isSearchActive,
}: CompanyFilterHeaderProps) => {
  return (
    <div className="flex flex-col md:flex-row justify-between items-end gap-4 border-b border-slate-800 pb-4">
      <div>
        <h1 className="text-3xl font-bold text-white">"Companies"</h1>
        <p className="text-slate-400 mt-2 text-sm">
          "나스닥/뉴욕증권거래소 상장 기업 전체 리스트"
        </p>
      </div>

      <div className="flex gap-3 w-full md:w-auto">
        {/* 정렬 드롭다운 */}
        <div className="relative">
          <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
            <Filter className="h-4 w-4 text-slate-400" />
          </div>
          <select
            value={sortOption}
            onChange={(e) => onSortChange(e.target.value)}
            disabled={isSearchActive}
            className="pl-9 pr-8 py-2.5 bg-slate-900 border border-slate-700 rounded-lg text-sm text-slate-200 focus:outline-none focus:border-blue-500 appearance-none cursor-pointer hover:bg-slate-800 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <option value="name">"이름순 (A-Z)"</option>
            <option value="score">"점수 높은순"</option>
          </select>
        </div>

        {/* 검색창 */}
        <div className="relative flex-1 md:w-64">
          <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
            <Search className="h-4 w-4 text-slate-400" />
          </div>
          <input
            type="text"
            placeholder="티커 또는 기업명..."
            className="block w-full pl-10 pr-4 py-2.5 bg-slate-900 border border-slate-700 rounded-lg text-sm text-slate-200 focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 transition-all placeholder-slate-500"
            value={searchTerm}
            onChange={(e) => onSearchChange(e.target.value)}
          />
        </div>
      </div>
    </div>
  );
};

export default CompanyFilterHeader;
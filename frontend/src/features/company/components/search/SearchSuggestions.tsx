import { CompanySummaryResponse } from "../../../../types/company";

interface SearchSuggestionsProps {
  suggestions: CompanySummaryResponse[];
  onSelect: (ticker: string) => void;
  isVisible: boolean;
}

export const SearchSuggestions = ({ suggestions, onSelect, isVisible }: SearchSuggestionsProps) => {
  if (!isVisible || suggestions.length === 0) return null;

  return (
    <div className="absolute top-full left-0 right-0 mt-2 bg-slate-800 border border-slate-700 rounded-lg shadow-xl overflow-hidden max-h-80 overflow-y-auto z-50">
      <ul>
        {suggestions.map((company) => (
          <li
            key={company.ticker}
            onClick={() => onSelect(company.ticker)}
            className="px-4 py-3 hover:bg-slate-700 cursor-pointer flex justify-between items-center group transition-colors border-b border-slate-700/50 last:border-0"
          >
            <div className="flex flex-col overflow-hidden">
              <span className="font-bold text-white group-hover:text-blue-400 transition-colors">
                {company.ticker}
              </span>
              <span className="text-xs text-slate-400 truncate">
                {company.name}
              </span>
            </div>
            <div className="flex flex-col items-end shrink-0">
              <span className="text-[10px] bg-slate-900 text-slate-400 px-1.5 py-0.5 rounded border border-slate-700">
                {company.exchange}
              </span>
              <span className="text-xs text-slate-500 mt-1">
                {company.sector}
              </span>
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
};
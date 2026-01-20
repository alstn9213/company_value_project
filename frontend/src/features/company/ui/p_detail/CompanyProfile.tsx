interface CompanyHeaderProps {
  ticker: string;
  name: string;
  exchange: string;
  sector: string;
}

export const CompanyProfile = ({ ticker, name, exchange, sector }: CompanyHeaderProps) => {
  return (
    <div className="flex items-center gap-6">
      {/* 로고 (티커 앞글자) */}
      <div className="w-16 h-16 rounded-2xl bg-slate-800 flex items-center justify-center text-2xl font-bold text-slate-200 shadow-inner">
        {ticker[0]}
      </div>
      {/* 회사이름, 티커, 거래소, 업종 */}
      <div>
        <div className="flex items-center gap-3">
          <h1 className="text-3xl font-bold text-white">
            {name}
          </h1>
          <span className="text-sm text-slate-400 bg-slate-800 px-2 py-1 rounded">
            {ticker}
          </span>
        </div>
        <div className="flex items-center gap-4 mt-2 text-slate-400 text-sm">
          <span className="flex items-center gap-1">
            {exchange}
          </span>
          <span>
            |
          </span>
          <span>
            {sector}
          </span>
        </div>
      </div>
    </div>
  );
};
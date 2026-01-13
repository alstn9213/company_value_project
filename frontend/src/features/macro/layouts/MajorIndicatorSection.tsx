import { MacroDataResponse } from "../../../types/macro";
import { IndicatorGrid } from "../ui/IndicatorGrid";

interface MajorIndicatorsProps {
  latest?: MacroDataResponse;
  isLoading: boolean;
}

export const MajorIndicatorSection = ({ latest, isLoading }: MajorIndicatorsProps) => {
  
  return (
    <section className="space-y-4 xl:col-span-2">
      {/* 공통 헤더 영역 */}
      <h2 className="text-lg font-bold text-slate-100 border-l-4 border-blue-500 pl-3">주요 지표</h2>

      <div className="flex flex-col gap-3">
        <IndicatorGrid data={latest} isLoading={isLoading}/>
      </div>
    </section>
  );
};

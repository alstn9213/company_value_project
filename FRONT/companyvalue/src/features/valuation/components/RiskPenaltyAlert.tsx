import { AlertTriangle } from "lucide-react";

interface Props {
  penaltyPoints: number;
}

const RiskPenaltyAlert = ({ penaltyPoints }: Props) => {
  if (penaltyPoints <= 0) return null;

  return (
    <div className="p-4 bg-orange-500/10 border border-orange-500/30 rounded-xl text-sm text-orange-200 animate-in fade-in slide-in-from-top-2 duration-300">
      <p className="font-bold flex items-center gap-2 mb-1">
        <AlertTriangle size={18} className="text-orange-400" />
        리스크 페널티 적용
      </p>
      <p className="opacity-80 pl-6 leading-relaxed">
        재무 건전성 이슈(자본잠식 또는 고부채 등)로 인해{" "}
        <strong>-{penaltyPoints}점</strong>의 페널티가 적용되었습니다.
      </p>
    </div>
  );
};

export default RiskPenaltyAlert;
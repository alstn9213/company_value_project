// 숫자 포맷터 (예: 150000000 -> $150M)
export const formatCurrency = (value: number | undefined): string => {
  if(value === undefined || value === null) return "-";

  const absValue = Math.abs(value);

  if (absValue >= 1.0e9) {
    return `$${(value / 1.0e9).toFixed(1)}B`; // Billion (10억)
  } else if (absValue >= 1.0e6) {
    return `$${(value / 1.0e6).toFixed(1)}M`; // Million (100만)
  } else if (absValue >= 1.0e3) {
    return `$${(value / 1.0e3).toFixed(1)}K`; // Thousand (1000)
  }
  return `$${value.toString()}`;
};

// 등급별 색상 반환
export const getGradeColor = (grade: string) => {
  switch (grade) {
    case "S": return "text-purple-400 border-purple-400 bg-purple-400/10";
    case "A": return "text-blue-400 border-blue-400 bg-blue-400/10";
    case "B": return "text-emerald-400 border-emerald-400 bg-emerald-400/10";
    case "C": return "text-yellow-400 border-yellow-400 bg-yellow-400/10";
    case "D": 
    case "F": return "text-red-400 border-red-400 bg-red-400/10";
    default: return "text-slate-400 border-slate-400 bg-slate-400/10";
  }
};

// 점수별 텍스트 색상
export const getScoreColor = (score: number) => {
  if (score >= 80) return "text-blue-400";
  if (score >= 60) return "text-emerald-400";
  if (score >= 40) return "text-yellow-400";
  return "text-red-400";
};
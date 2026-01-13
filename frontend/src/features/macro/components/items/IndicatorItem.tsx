import React from "react";

interface IndicatorItemProps {
  label: string;
  value: string;
  /** Lucide icon 컴포넌트 등을 받기 위한 ReactNode 타입 */
  icon: React.ReactNode;
  color: string;
  subText?: string;
  /** 경고 스타일 적용 여부 */
  isAlert?: boolean;
}

export const IndicatorItem = ({
  label,
  value,
  icon,
  color,
  subText,
  isAlert = false,
}: IndicatorItemProps) => {
  return (
    <div
      className={`group flex flex-col gap-1 rounded-lg border p-4 transition-all ${
        isAlert
          ? "border-red-500/50 bg-red-500/10"
          : "border-slate-700 bg-slate-800 hover:border-slate-600"
      }`}
    >
      <div className="flex items-center justify-between">
        <span className="text-sm font-medium text-slate-400">{label}</span>
        <div className={`opacity-70 ${color}`}>{icon}</div>
      </div>
      <div className={`text-2xl font-bold ${color}`}>{value}</div>
      {subText && <div className="text-xs text-slate-500">{subText}</div>}
    </div>
  );
};

import { ReactNode } from "react";
import { cn } from "../../utils/cn";

interface ChartCardProps {
  children: ReactNode;
  className?: string; // 추가적인 스타일 오버라이딩을 위해
  centerContent?: boolean; // 중앙 정렬 여부 플래그
}

export const ChartCard = ({ children, className, centerContent = false }: ChartCardProps) => {
  return (
    <div 
      className={cn(
        // 공통 기본 스타일
        "w-full h-[350px] bg-slate-800/30 rounded-xl border border-slate-700/50",
        "animate-in fade-in slide-in-from-bottom-4 duration-500 mb-4",
        // 조건부 스타일 (중앙 정렬 vs 세로 배치)
        centerContent ? "flex items-center justify-center" : "flex flex-col p-4",
        // 사용자 지정 스타일 병합
        className
      )}
    >
      {children}
    </div>
  );
};
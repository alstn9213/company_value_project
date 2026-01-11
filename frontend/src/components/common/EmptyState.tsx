interface EmptyStateProps {
  icon?: React.ReactNode;   // 아이콘을 유동적으로 받을 수 있게
  title?: string;           // 강조된 메시지
  description?: string;     // 부가 설명
  className?: string;       // 추가 스타일링
}

const EmptyState = ({ 
  icon, title = "데이터가 없습니다.",
  description, className
 }: EmptyStateProps) => {
  return (
    <div className={`flex flex-col items-center justify-center text-center p-4 ${className}`}>
      {icon && <div className="mb-3 opacity-50">{icon}</div>}
      <p className="text-slate-400 font-medium">{title}</p>
      {description && <p className="text-sm text-slate-500 mt-1">{description}</p>}
    </div>
  );
};
export default EmptyState;
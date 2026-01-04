import { AlertTriangle, RefreshCw } from "lucide-react";

interface ErrorStateProps {
  title?: string;
  message?: string;
  onRetry?: () => void; // 재시도 함수를 prop으로 받음 (React Query의 refetch 등)
  className?: string;
}

const ErrorState = ({ 
  title = "오류가 발생했습니다", 
  message = "데이터를 불러오는 중 문제가 발생했습니다. 잠시 후 다시 시도해주세요.", 
  onRetry,
  className 
}: ErrorStateProps) => {
  return (
    <div className={`flex flex-col items-center justify-center text-center p-8 py-12 ${className}`}>
      <div className="bg-red-500/10 p-4 rounded-full mb-4">
        <AlertTriangle className="w-8 h-8 text-red-500" />
      </div>
      <h3 className="text-lg font-semibold text-slate-200 mb-2">{title}</h3>
      <p className="text-sm text-slate-400 max-w-md mb-6 whitespace-pre-line">
        {message}
      </p>
      
      {onRetry && (
        <button 
          onClick={onRetry}
          className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-red-600 hover:bg-red-700 rounded-lg transition-colors active:scale-95"
        >
          <RefreshCw className="w-4 h-4" />
          다시 시도
        </button>
      )}
    </div>
  );
};

export default ErrorState;
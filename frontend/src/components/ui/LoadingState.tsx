interface LoadingStateProps {
  message?: string;
}

export const LoadingState = ({ message = "데이터를 불러오는 중입니다..." }: LoadingStateProps) => {
    return (
    <div className="text-center py-20">
      <div className="inline-block animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-blue-500 mb-4"></div>
      <p className="text-slate-400">{message}</p>
    </div>
  );
};

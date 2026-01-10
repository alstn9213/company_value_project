interface SkeletonProps {
  className?: string;
}

const Skeleton = ({ className }: SkeletonProps) => {
  // Tailwind의 animate-pulse를 기본으로 깔고, 외부에서 크기(w, h)와 모양(rounded)을 제어하게 합니다.
  return <div className={`bg-slate-800 animate-pulse ${className}`} />;
};

export default Skeleton;
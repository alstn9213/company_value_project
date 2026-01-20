import { Skeleton } from "../../../../components/ui/Skeleton";
import { cn } from "../../../../utils/cn";

interface Props {
  className?: string;
}

export const MajorIndicatorSkeleton = ({ className }: Props) => (
  <section className={cn("space-y-6", className)}>
    <div className="h-8 w-48 bg-slate-200 rounded animate-pulse" />
    <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
      {[...Array(4)].map((_, i) => (
        <Skeleton key={i} className="h-32 w-full rounded-xl" />
      ))}
    </div>
    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
      <Skeleton className="h-48 w-full rounded-xl" />
      <Skeleton className="h-48 w-full rounded-xl" />
    </div>
  </section>
);
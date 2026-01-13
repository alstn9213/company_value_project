import { Skeleton } from "../../../../components/ui/Skeleton";

export const RankingSkeleton = () => {
  return (
    <>
      {[1, 2, 3, 4, 5, 6, 7, 8, 9, 10].map((i) => (
        <tr key={i}>
          <td className="py-3 pl-4">
            <Skeleton className="h-4 w-4 rounded bg-slate-700" />
          </td>
          <td className="py-3">
            <div className="space-y-1">
              <Skeleton className="h-4 w-12 rounded bg-slate-700" />
              <Skeleton className="h-3 w-20 rounded bg-slate-700" />
            </div>
          </td>
          <td className="py-3 text-center">
            <Skeleton className="mx-auto h-4 w-8 rounded bg-slate-700" />
          </td>
          <td className="py-3 text-center">
            <Skeleton className="mx-auto h-5 w-8 rounded bg-slate-700" />
          </td>
        </tr>
      ))}
    </>
  );
};
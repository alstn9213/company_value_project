import { LucideIcon, Search } from "lucide-react";

interface EmptyStateProps {
  icon?: LucideIcon;
  title?: string;
  description?: string;
}

const EmptyState = ({
  icon: Icon = Search,
  title = "데이터가 없습니다.",
  description,
}: EmptyStateProps) => {
  return (
    <div className="text-center py-20 bg-slate-800/30 rounded-xl border border-slate-700/50">
      <Icon className="mx-auto h-12 w-12 text-slate-500 mb-4" />
      <p className="text-slate-300 text-lg">{title}</p>
      {description && <p className="text-slate-500">{description}</p>}
    </div>
  );
};

export default EmptyState;
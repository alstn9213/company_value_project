import { CompanySummaryResponse } from "../../../../types/company";
import { CompanyCardSkeleton } from "../../ui/skeletons/CompanyCardSkeleton";
import { CompanyCard } from "./CompanyCard";
import { EmptyState } from "../../../../components/ui/EmptyState";

interface CompanyGridProps {
  companies?: CompanySummaryResponse[];
  isLoading: boolean;
}

export const CompanyGrid = ({ companies, isLoading }: CompanyGridProps) => {

  if (isLoading) {
    return (
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-5">
        {Array.from({ length: 8 }).map((_, index) => (
          <CompanyCardSkeleton key={index} />
        ))}
      </div>
    );
  }
  
  if (!companies || companies.length === 0) {
    return (
      <EmptyState
        title="검색 결과가 없습니다."
        description="다른 키워드로 검색해보세요."
      />
    );
  }

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-5">
      {companies.map((company) => (
      <CompanyCard 
        key={company.ticker}
        company={company} 
      />
      ))}
    </div>
  );
};

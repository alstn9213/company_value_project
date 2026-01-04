import { Company } from "../../../types/company";
import EmptyState from "../../../components/common/EmptyState";
import CompanyCardSkeleton from "./skeletons/CompanyCardSkeleton";
import CompanyCard from "./CompanyCard";

interface CompanyGridSectionProps {
  isLoading: boolean;
  companies?: Company[];
}

const CompanyGridSection = ({ isLoading, companies }: CompanyGridSectionProps) => {
  // 로딩
  if (isLoading) {
    return (
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-5">
        {Array.from({ length: 8 }).map((_, index) => (
          <CompanyCardSkeleton key={index} />
        ))}
      </div>
    );
  }
  
  // 데이터 없음
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
        <CompanyCard key={company.ticker} company={company} />
      ))}
      </div>

  );
};

export default CompanyGridSection;
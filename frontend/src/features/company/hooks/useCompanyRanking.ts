import { useQuery } from "@tanstack/react-query";
import { companyApi } from "../api/companyApi";

export const useTopRankingCompanies = () => {
  return useQuery({
    queryKey: ["topRanked"],
    queryFn: companyApi.getTopRanked,
    // 필요한 경우 select 옵션을 통해 데이터 가공 로직 추가 가능
  });
};
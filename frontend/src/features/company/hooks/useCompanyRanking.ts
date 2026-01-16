import { useQuery } from "@tanstack/react-query";
import { companyApi } from "../api/companyApi";

export const useTopRankingCompanies = () => {
  return useQuery({
    queryKey: ["topRanked"],
    queryFn: companyApi.getTopRanked,
  });
};
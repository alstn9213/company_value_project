export const companyKeys = {
  all: ["company"] as const,

  lists: () => [...companyKeys.all, "list"] as const,
  // 리스트는 필터(page, sort)에 따라 달라지므로 객체 형태로 포함시키는 것이 관례.
  list: (page: number, size: number, sort: string) => [...companyKeys.lists(), { page, size, sort }] as const,

  details: () => [...companyKeys.all, "detail"] as const,
  detail: (ticker: string) => [...companyKeys.details(), ticker] as const,

  searches: () => [...companyKeys.all, "search"] as const,
  search: (keyword: string) => [...companyKeys.searches(), keyword] as const,

  rankings: () => [...companyKeys.all, "ranking"] as const,
  topRanking: () => [...companyKeys.rankings(), "top"] as const,

  stocks: () => [...companyKeys.all, "stock"] as const,
  stock: (ticker: string) => [...companyKeys.stocks(), ticker] as const,
};
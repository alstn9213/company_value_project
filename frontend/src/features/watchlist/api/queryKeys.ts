export const watchlistKeys = {
  all: ["watchlist"] as const,  
  lists: () => [...watchlistKeys.all, "list"] as const,
};
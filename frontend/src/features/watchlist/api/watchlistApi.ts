import axiosClient from "../../../api/axiosClient";
import { WatchlistResponse } from "../../../types/watchlist";

export const watchlistApi = {
  getMyWatchlist: async (): Promise<WatchlistResponse[]> => {
    const response = await axiosClient.get<WatchlistResponse[]>("/api/watchlist");
    return response.data;
  },
  
  add: async (ticker: string): Promise<void> => {
    await axiosClient.post<void>(`/api/watchlist/${ticker}`);
  },

  remove: async (watchlistId: number): Promise<void> => {
    await axiosClient.delete<void>(`/api/watchlist/${watchlistId}`);
  }
}
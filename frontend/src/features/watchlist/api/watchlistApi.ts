import axiosClient from "../../../api/axiosClient";
import { WatchlistResponse } from "../../../types/watchlist";

export const watchlistApi = {
  getMyWatchlist: async (): Promise<WatchlistResponse[]> => {
    const response = await axiosClient.get<WatchlistResponse[]>("/api/watchlist");
    return response.data;
  },
  
  add: async (ticker: string): Promise<string> => {
    const response = await axiosClient.post<string>(`/api/watchlist/${ticker}`);
    return response.data;
  },

  remove: async (watchlistId: number): Promise<string> => {
    const response = await axiosClient.delete<string>(`/api/watchlist/${watchlistId}`);
    return response.data;
  }
}
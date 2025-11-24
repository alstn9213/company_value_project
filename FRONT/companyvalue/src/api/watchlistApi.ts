import { WatchlistItem } from "../types/watchlist";
import axiosClient from "./axiosClient";

export const watchlistApi = {
  getMyWatchlist: async (): Promise<WatchlistItem[]> => {
    const response = await axiosClient.get<WatchlistItem[]>("/api/watchlist");
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
import { CompanySummaryResponse } from "./company";

export interface WatchlistItem {
  watchlistId: number;
  company: CompanySummaryResponse;
}
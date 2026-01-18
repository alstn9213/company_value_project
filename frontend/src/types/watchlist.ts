import { CompanySummaryResponse } from "./company";

export interface WatchlistResponse {
  watchlistId: number;
  company: CompanySummaryResponse;
}
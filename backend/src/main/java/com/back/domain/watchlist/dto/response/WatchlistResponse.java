package com.back.domain.watchlist.dto;

import com.back.domain.company.dto.response.CompanySummaryResponse;
import com.back.domain.watchlist.entity.Watchlist;

public record WatchlistResponse(
        Long watchlistId,
        CompanySummaryResponse company
) {
  public static WatchlistResponse from(Watchlist watchlist) {
    return new WatchlistResponse(
            watchlist.getId(),
            CompanySummaryResponse.from(watchlist.getCompany())
    );
  }
}
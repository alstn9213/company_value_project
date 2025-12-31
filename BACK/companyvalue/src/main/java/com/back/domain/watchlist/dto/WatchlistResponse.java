package com.back.domain.watchlist.dto;

public record WatchlistResponse(
        Long watchlistId,
        String ticker,
        String name,
        Integer currentScore,
        String currentGrade
) {

}
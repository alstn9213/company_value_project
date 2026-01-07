package com.back.domain.watchlist.dto;

import com.back.domain.company.entity.Company;
import com.back.domain.company.entity.CompanyScore;
import com.back.domain.watchlist.entity.Watchlist;

public record WatchlistResponse(
        Long watchlistId,
        String ticker,
        String name,
        Integer currentScore,
        String currentGrade
) {
  public static WatchlistResponse from(Watchlist watchlist) {
    Company company = watchlist.getCompany();
    CompanyScore score = company.getCompanyScore();

    // 점수가 아직 계산되지 않은 경우(null)에 대한 방어 로직
    int totalScore = (score != null) ? score.getTotalScore() : 0;
    String grade = (score != null) ? score.getGrade() : "-";

    return new WatchlistResponse(
            watchlist.getId(),
            company.getTicker(),
            company.getName(),
            totalScore,
            grade
    );
  }
}
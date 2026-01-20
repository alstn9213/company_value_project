package com.back.api.watchlist;

import com.back.domain.watchlist.dto.response.WatchlistResponse;
import com.back.global.annotation.CurrentMemberId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "Watchlist API", description = "관심 종목 관리 API")
public interface WatchlistControllerDocs {

  @Operation(summary = "관심 종목 목록 조회", description = "사용자의 관심 종목 목록을 조회합니다.")
  ResponseEntity<List<WatchlistResponse>> getMyWatchlist(
          @Parameter(hidden = true)
          @CurrentMemberId Long memberId
  );

  @Operation(summary = "관심 종목 추가", description = "관심 종목에 새로운 기업을 추가합니다.")
  ResponseEntity<Void> addWatchlist(
          @Parameter(hidden = true)
          @CurrentMemberId Long memberId,
          @Parameter(description = "추가할 기업 티커", example = "AAPL")
          @PathVariable String ticker
  );

  @Operation(summary = "관심 종목 삭제", description = "관심 종목에서 특정 기업을 삭제합니다.")
  ResponseEntity<String> deleteWatchlist(
          @Parameter(hidden = true)
          @CurrentMemberId Long memberId,
          @Parameter(hidden = true)
          @PathVariable Long watchlistId
  );
}
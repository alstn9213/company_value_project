package com.back.api.watchlist;


import com.back.domain.watchlist.dto.response.WatchlistResponse;
import com.back.domain.watchlist.service.WatchlistService;
import com.back.global.annotation.CurrentMemberId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/watchlist")
@RequiredArgsConstructor
public class WatchlistController implements WatchlistControllerDocs {

  private final WatchlistService watchlistService;

  @Override
  @GetMapping
  public ResponseEntity<List<WatchlistResponse>> getMyWatchlist(@CurrentMemberId Long memberId) {
    return ResponseEntity.ok(watchlistService.getWatchlist(memberId));
  }

  @Override
  @PostMapping("/{ticker}")
  public ResponseEntity<Void> addWatchlist(@CurrentMemberId Long memberId, @PathVariable String ticker) {
    watchlistService.addWatchlist(memberId, ticker);
    return ResponseEntity.ok().build();
  }

  @Override
  @DeleteMapping("/{watchlistId}")
  public ResponseEntity<String> deleteWatchlist(@CurrentMemberId Long memberId, @PathVariable Long watchlistId) {
    watchlistService.deleteWatchlist(memberId, watchlistId);
    return ResponseEntity.ok().build();
  }

}

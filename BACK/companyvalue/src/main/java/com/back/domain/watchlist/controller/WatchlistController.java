package com.back.domain.watchlist.controller;


import com.back.domain.watchlist.dto.WatchlistResponse;
import com.back.domain.watchlist.service.WatchlistService;
import com.back.global.annotation.CurrentMemberId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/watchlist")
@RequiredArgsConstructor
public class WatchlistController {

    private final WatchlistService watchlistService;

    @GetMapping
    public ResponseEntity<List<WatchlistResponse>> getMyWatchlist(@CurrentMemberId Long memberId) {
        return ResponseEntity.ok(watchlistService.getWatchlist(memberId));
    }

    @PostMapping("/{ticker}")
    public ResponseEntity<String> addWatchlist(@CurrentMemberId Long memberId, @PathVariable String ticker) {
        watchlistService.addWatchlist(memberId, ticker);
        return ResponseEntity.ok("관심 종목 추가 완료");
    }

    @DeleteMapping("/{watchlistId}")
    public ResponseEntity<String> deleteWatchlist(@CurrentMemberId Long memberId, @PathVariable Long watchlistId) {
        watchlistService.deleteWatchlist(memberId, watchlistId);
        return ResponseEntity.ok("삭제 완료");
    }

}

package com.back.domain.watchlist.controller;


import com.back.domain.watchlist.dto.WatchlistResponse;
import com.back.domain.watchlist.service.WatchlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/watchlist")
@RequiredArgsConstructor
public class WatchlistController {

    private final WatchlistService watchlistService;

    @GetMapping
    public ResponseEntity<List<WatchlistResponse>> getMyWatchlist(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(watchlistService.getWatchlist(getMemberId(user)));
    }

    @PostMapping("/{ticker}")
    public ResponseEntity<String> addWatchlist(@AuthenticationPrincipal User user, @PathVariable String ticker) {
        watchlistService.addWatchlist(getMemberId(user), ticker);
        return ResponseEntity.ok("관심 종목 추가 완료");
    }

    @DeleteMapping("/{watchlistId}")
    public ResponseEntity<String> deleteWatchlist(@AuthenticationPrincipal User user, @PathVariable Long watchlistId) {
        watchlistService.deleteWatchlist(getMemberId(user), watchlistId);
        return ResponseEntity.ok("삭제 완료");
    }

    private Long getMemberId(User user) {
        if (user == null) throw new RuntimeException("로그인이 필요합니다."); // Security Filter에서 걸러지겠지만 방어 코드
        return Long.parseLong(user.getUsername());
    }
}

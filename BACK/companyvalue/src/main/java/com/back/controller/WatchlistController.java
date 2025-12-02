package com.back.controller;

import com.back.domain.Company;
import com.back.domain.CompanyScore;
import com.back.domain.Member;
import com.back.domain.Watchlist;
import com.back.domain.repository.CompanyRepository;
import com.back.domain.repository.CompanyScoreRepository;
import com.back.domain.repository.MemberRepository;
import com.back.domain.repository.WatchlistRepository;
import com.back.dto.MainResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/watchlist")
@RequiredArgsConstructor
public class WatchlistController {

    private final WatchlistRepository watchlistRepository;
    private final MemberRepository memberRepository;
    private final CompanyRepository companyRepository;
    private final CompanyScoreRepository companyScoreRepository;


    // 1. 내 관심 종목 조회
    @GetMapping
    public ResponseEntity<List<MainResponseDto.WatchlistResponse>> getMyWatchlist(@AuthenticationPrincipal User user) {
        if (user == null) return ResponseEntity.status(401).build();

        Member member = memberRepository.findById(Long.parseLong(user.getUsername()))
                .orElseThrow(() -> new RuntimeException("회원 정보 없음"));

        List<MainResponseDto.WatchlistResponse> list = watchlistRepository.findByMember(member).stream()
                .map(w -> {
                    // 점수 정보 함께 조회 (없으면 0점)
                    CompanyScore score = companyScoreRepository.findByCompany(w.getCompany())
                            .orElse(CompanyScore.builder().totalScore(0).grade("-").build());

                    return new MainResponseDto.WatchlistResponse(
                            w.getId(),
                            w.getCompany().getTicker(),
                            w.getCompany().getName(),
                            score.getTotalScore(),
                            score.getGrade()
                    );
                })
                .toList();

        return ResponseEntity.ok(list);
    }

    // 2. 관심 종목 추가
    @PostMapping("/{ticker}")
    @Transactional
    public ResponseEntity<String> addWatchlist(@AuthenticationPrincipal User user, @PathVariable String ticker) {
        Member member = memberRepository.findById(Long.parseLong(user.getUsername()))
                .orElseThrow(() -> new RuntimeException("회원 정보 없음"));

        Company company = companyRepository.findByTicker(ticker)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 기업"));

        if (watchlistRepository.existsByMemberAndCompanyTicker(member, ticker)) {
            return ResponseEntity.badRequest().body("이미 관심 목록에 존재합니다.");
        }

         Watchlist watchlist = Watchlist.builder()
                 .member(member)
                 .company(company)
                 .build();

         watchlistRepository.save(watchlist);

        return ResponseEntity.ok("관심 종목 추가 완료");
    }

    // 3. 관심 종목 삭제
    @DeleteMapping("/{watchlistId}")
    public ResponseEntity<String> deleteWatchlist(@AuthenticationPrincipal User user, @PathVariable Long watchlistId) {
        watchlistRepository.deleteById(watchlistId);
        return ResponseEntity.ok("삭제 완료");
    }
}

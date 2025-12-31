package com.back.domain.company.controller;

import com.back.domain.company.dto.response.CompanyScoreResponse;
import com.back.domain.company.repository.CompanyScoreRepository;
import com.back.domain.company.service.analysis.ScoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/scores")
@RequiredArgsConstructor
public class ScoreController {

    private final CompanyScoreRepository companyScoreRepository;
    private final ScoringService scoringService;

    // 상위 10개 우량 기업 조회
    @GetMapping("/top")
    public ResponseEntity<List<CompanyScoreResponse>> getTopRankedCompanies() {
        List<CompanyScoreResponse> topCompanies = companyScoreRepository.findTop10ByOrderByTotalScoreDesc()
                .stream()
                .map(CompanyScoreResponse::from)
                .toList();
        return ResponseEntity.ok(topCompanies);
    }

    @GetMapping("/{ticker}")
    public ResponseEntity<CompanyScoreResponse> getCompanyScore(@PathVariable String ticker) {
        // 서비스 호출 (여기서 캐싱된 DTO를 가져옴)
        CompanyScoreResponse result = scoringService.getScoreByTicker(ticker);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }
}

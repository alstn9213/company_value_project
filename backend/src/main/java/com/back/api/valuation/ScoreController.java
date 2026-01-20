package com.back.api.valuation;

import com.back.domain.company.dto.response.CompanyScoreResponse;
import com.back.domain.valuation.engine.ScoringService;
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
public class ScoreController implements ScoreControllerDocs {

  private final ScoringService scoringService;

  @Override
  @GetMapping("/top")
  public ResponseEntity<List<CompanyScoreResponse>> getTopRankedCompanies() {
    return ResponseEntity.ok(scoringService.getTopRankedCompanies());
  }

  @Override
  @GetMapping("/{ticker}")
  public ResponseEntity<CompanyScoreResponse> getCompanyScore(@PathVariable String ticker) {
    return ResponseEntity.ok(scoringService.getScoreByTicker(ticker));
  }
}

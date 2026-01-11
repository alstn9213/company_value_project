package com.back.infra.scheduler.controller;

import com.back.domain.company.service.ScoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/test/company")
@RequiredArgsConstructor
public class CompanyTestController {

  private final ScoringService scoringService;

  /**
   * 저장된 데이터를 기반으로 점수만 다시 계산
   * URL: http://localhost:8080/test/company/re-score
   */
  @GetMapping("/re-score") // 브라우저 접속(GET) 허용
  public ResponseEntity<String> recalculateAllScores() {
    log.info("관리자 요청에 의해 모든 기업의 점수 재산정을 시작합니다.");

    scoringService.calculateScoresAndSave();

    return ResponseEntity.ok("모든 기업의 점수 재산정이 완료되었습니다.");
  }
}
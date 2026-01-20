package com.back.api.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Company Test API", description = "로컬 개발용 기업 API")
public interface CompanyTestControllerDocs {

  @Operation(summary = "점수 재산정", description = "기업의 점수를 재산정합니다.")
  ResponseEntity<String> recalculateAllScores();
}

package com.back.api.valuation;

import com.back.domain.company.dto.response.CompanyScoreResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "Score API", description = "기업 점수 및 등수 조회 API")
public interface ScoreControllerDocs {

  @Operation(summary = "기업 등수 조회", description = "점수 상위 10등까지의 기업을 조회합니다.")
  ResponseEntity<List<CompanyScoreResponse>> getTopRankedCompanies();

  @Operation(summary = "기업 점수 조회", description = "기업의 점수를 조회합니다.")
  ResponseEntity<CompanyScoreResponse> getCompanyScore(
          @Parameter(description = "기업의 티커", example = "AAPL", required = true)
          @PathVariable String ticker);
}

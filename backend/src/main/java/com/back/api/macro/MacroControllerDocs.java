package com.back.api.macro;

import com.back.domain.macro.dto.MacroDataResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Macro API", description = "거시 경제 지표 조회 API")
public interface MacroControllerDocs {

  @Operation(summary = "최신 경제 지표 조회", description = "최신 경제 지표를 가져옵니다.")
  ResponseEntity<MacroDataResponse> getLatestMacroData();

  @Operation(summary = "경제 지표 조회", description = "최근 10년간의 경제 지표를 가져옵니다.")
  ResponseEntity<List<MacroDataResponse>> getMacroHistory();
}

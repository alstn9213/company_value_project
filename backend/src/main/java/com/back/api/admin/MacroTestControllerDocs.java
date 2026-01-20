package com.back.api.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Macro Test API", description = "로컬 개발용 거시 경제 API")
public interface MacroTestControllerDocs {

  @Operation(summary = "거시 경제 지표 초기화", description = "DB를 구성하기 위해 기존 정보를 초기화하고 자료를 수집합니다.")
  String initMacroHistory();

  @Operation(summary = "거시 경제 지표 업데이트", description = "DB 구성 이후 날짜의 거시 경제 자료를 수집합니다.")
  String updateDailyMacro();
}

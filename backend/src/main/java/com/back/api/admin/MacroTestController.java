package com.back.api.admin;

import com.back.domain.macro.service.MacroDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/test/macro")
@RequiredArgsConstructor
public class MacroTestController implements MacroTestControllerDocs {

  private final MacroDataService macroDataService;

  /**
   * URL: http://localhost:8080/test/macro/init
   */
  @Override
  @GetMapping("/init")
  public String initMacroHistory() {
    macroDataService.initHistoricalMacroData();
    return "거시 경제 과거 데이터 초기화 완료.";
  }

  /**
   * URL: http://localhost:8080/test/macro/update-daily
   */
  @Override
  @GetMapping("/update-daily")
  public String updateDailyMacro() {
    log.info(">>> [Test] 거시 경제 데이터 일일 업데이트 요청");
    try {
      macroDataService.updateMacroEconomicData();
      return "거시 경제 데이터(금리/물가/실업률) 업데이트 및 캐시 초기화 완료.";
    } catch (Exception e) {
      log.error("업데이트 실패", e);
      return "업데이트 실패: " + e.getMessage();
    }
  }
}


package com.back.api.macro;

import com.back.domain.macro.dto.MacroDataResponse;
import com.back.domain.macro.service.MacroReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/macro")
@RequiredArgsConstructor
public class MacroController implements MacroControllerDocs {

  private final MacroReadService macroReadService;

  @Override
  @GetMapping("/latest")
  public ResponseEntity<MacroDataResponse> getLatestMacroData() {
      return ResponseEntity.ok(macroReadService.getLatestData());
  }

  @Override
  @GetMapping("/history")
  public ResponseEntity<List<MacroDataResponse>> getMacroHistory() {
      return ResponseEntity.ok(macroReadService.getHistoryData());
  }

}

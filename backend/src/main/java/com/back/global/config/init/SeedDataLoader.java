package com.back.global.config.init;

import com.back.global.config.init.dto.CompanySeedDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeedDataLoader {

  private final ObjectMapper objectMapper;

  // JSON 파일 로드 및 파싱
  public List<CompanySeedDto> loadSeedData() {
    try {
      ClassPathResource resource = new ClassPathResource("data/seed_data.json");
      if (!resource.exists()) {
        log.warn("시드 데이터 파일이 없습니다. (src/main/resources/data/seed_data.json)");
        return Collections.emptyList();
      }
      try (InputStream inputStream = resource.getInputStream()) {
        return objectMapper.readValue(inputStream, new TypeReference<List<CompanySeedDto>>() {});
      }
    } catch (Exception e) {
      throw new RuntimeException("초기 데이터 로딩 중 오류 발생", e);
    }
  }





}

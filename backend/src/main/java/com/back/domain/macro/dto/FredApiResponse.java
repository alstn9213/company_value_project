package com.back.domain.macro.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true) // 정의하지 않은 필드는 무시
public record FredApiResponse(
        @JsonProperty("observations") List<FredObservation> observations
) {
  // 내부 배열의 요소 DTO
  @JsonIgnoreProperties(ignoreUnknown = true)
  public record FredObservation(
          @JsonProperty("date") String date,
          @JsonProperty("value") String value
  ) {

  }
}

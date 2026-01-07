package com.back.global.error;

import com.back.global.error.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  // 비즈니스 로직 에러 처리 (미리 정의한 예외)
  @ExceptionHandler(BusinessException.class)
  protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
    log.error("정의된 예외처리: {}", e.getErrorCode());
    return ResponseEntity
            .status(e.getErrorCode().getStatus())
            .body(ErrorResponse.of(e.getErrorCode()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  protected ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
    log.warn("잘못된 입력값 감지: {}", e.getMessage());
    return ResponseEntity
            .status(ErrorCode.INVALID_INPUT_VALUE.getStatus())
            .body(ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE));
  }

  // 그 외 모든 예상치 못한 에러 처리
  @ExceptionHandler(Exception.class)
  protected ResponseEntity<ErrorResponse> handleException(Exception e) {
    log.error("예상치 못한 에러: ", e);
    return ResponseEntity
            .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
            .body(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR));
  }
}

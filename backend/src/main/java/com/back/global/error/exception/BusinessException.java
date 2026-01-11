package com.back.global.error.exception;

import com.back.global.error.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

  private final ErrorCode errorCode;

  public BusinessException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  // 원본 예외(cause)를 함께 받는 생성자
  public BusinessException(ErrorCode errorCode, Throwable cause) {
    super(errorCode.getMessage(), cause); // 부모(RuntimeException)에 메시지와 원본 에러 전달
    this.errorCode = errorCode;
  }
}

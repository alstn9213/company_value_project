package com.back.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  // Common
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),
  JSON_PARSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "데이터 파싱 중 오류가 발생했습니다."),
  INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력입니다."),

  // Member
  MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND,  "회원 정보를 찾을 수 없습니다."),
  EMAIL_DUPLICATION(HttpStatus.CONFLICT,  "이미 가입된 이메일입니다."),
  LOGIN_FAILED(HttpStatus.UNAUTHORIZED,  "이메일 또는 비밀번호가 일치하지 않습니다."),
  LOGIN_REQUIRED(HttpStatus.UNAUTHORIZED,"로그인이 필요한 서비스입니다."),

  // Company
  COMPANY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 기업을 찾을 수 없습니다."),
  COMPANY_SCORE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 기업의 분석 점수 데이터가 존재하지 않습니다."),
  REQUIRED_DATA_MISSING(HttpStatus.BAD_REQUEST, "기업 등록을 위한 필수 데이터(재무제표 또는 주가)가 누락되었습니다."),

  // Financial
  FINANCIAL_STATEMENT_NOT_FOUND(HttpStatus.NOT_FOUND,  "해당 기업의 재무제표 데이터를 찾을 수 없습니다."),

  // Stock
  LATEST_STOCK_NOT_FOUND(HttpStatus.NOT_FOUND,  "최신 주가 데이터를 찾을 수 없습니다."),

  // Macro
  MACRO_DATA_NOT_FOUND(HttpStatus.NOT_FOUND, "거시 경제 데이터를 찾을 수 없습니다."),
  BOND_YIELD_NOT_FOUND(HttpStatus.NOT_FOUND, "10년물 채권 금리 데이터가 누락되었습니다."),
  INVALID_MACRO_VALUE(HttpStatus.BAD_REQUEST,  "거시 경제 지표 값은 음수일 수 없습니다."),

  // Watchlist
  WATCHLIST_DUPLICATION(HttpStatus.BAD_REQUEST, "이미 관심 목록에 존재합니다."),
  WATCHLIST_ACCESS_DENIED(HttpStatus.FORBIDDEN,  "해당 관심 종목에 대한 접근 권한이 없습니다."),

  // Auth (Token)
  INVALID_TOKEN(HttpStatus.UNAUTHORIZED,  "유효하지 않은 토큰입니다."),
  EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED,  "만료된 토큰입니다."),
  UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "지원하지 않는 토큰 형식입니다."),
  TOKEN_CLAIMS_EMPTY(HttpStatus.UNAUTHORIZED,  "토큰에 권한 정보가 존재하지 않습니다."),
  TOKEN_SIGNATURE_INVALID(HttpStatus.UNAUTHORIZED,  "토큰 서명이 유효하지 않습니다.");


  private final HttpStatus status;
  private final String message;

  public String getCode() {
    return this.name();
  }

}
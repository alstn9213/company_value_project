package com.back.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C001", "서버 내부 오류입니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C002", "잘못된 입력입니다."),

    // Member
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "M001", "회원 정보를 찾을 수 없습니다."),
    EMAIL_DUPLICATION(HttpStatus.CONFLICT, "M002", "이미 가입된 이메일입니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "M003", "이메일 또는 비밀번호가 일치하지 않습니다."),

    // Company
    COMPANY_NOT_FOUND(HttpStatus.NOT_FOUND, "CP001", "해당 기업을 찾을 수 없습니다."),

    // Financial
    INVALID_FINANCIAL_DATA(HttpStatus.BAD_REQUEST, "F001", "유효하지 않은 재무 데이터입니다 (매출액, 자본, 부채 누락 등)."),

    // Stock
    LATEST_STOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "최신 주가 데이터를 찾을 수 없습니다."),

    // Macro
    MACRO_DATA_NOT_FOUND(HttpStatus.NOT_FOUND, "MAC001", "거시 경제 데이터를 찾을 수 없습니다."),
    BOND_YIELD_NOT_FOUND(HttpStatus.NOT_FOUND, "MAC002", "채권 금리 데이터가 누락되었습니다."),

    // Watchlist
    WATCHLIST_DUPLICATION(HttpStatus.BAD_REQUEST, "W001", "이미 관심 목록에 존재합니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;
}
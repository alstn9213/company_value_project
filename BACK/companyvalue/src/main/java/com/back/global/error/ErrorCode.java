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

    // Watchlist
    WATCHLIST_DUPLICATION(HttpStatus.BAD_REQUEST, "W001", "이미 관심 목록에 존재합니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
package com.back.domain.auth.dto.response;

public record TokenResponse(
        String accessToken,
        String tokenType,
        Long expiresIn,
        String nickname // 프론트엔드 표시용
) {
    public static TokenResponse of(String accessToken, String nickname, Long expiresIn) {
        return new TokenResponse(accessToken, "Bearer", expiresIn, nickname);
    }
}
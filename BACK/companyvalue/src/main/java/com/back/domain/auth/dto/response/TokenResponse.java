package com.back.domain.auth.dto.response;

public record TokenResponse(
        String accessToken,
        String tokenType,
        Long expiresIn,
        String nickname // 프론트엔드 표시용
) {
    // 필요하다면 팩토리 메서드를 추가하여 유연하게 생성 가능
    public static TokenResponse of(String accessToken, String nickname, Long expiresIn) {
        return new TokenResponse(accessToken, "Bearer", expiresIn, nickname);
    }
}
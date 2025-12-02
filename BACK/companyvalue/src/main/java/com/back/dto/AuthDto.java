package com.back.dto;

public class AuthDto {

    // 회원가입 요청
    public record SignUpRequest(
            String email,
            String password,
            String nickname
    ) {
    }

    // 로그인 요청
    public record LoginRequest(
            String email,
            String password
    ) {
    }

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
}

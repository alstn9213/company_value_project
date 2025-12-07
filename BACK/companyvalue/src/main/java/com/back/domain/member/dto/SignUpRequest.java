package com.back.domain.member.dto;

public record SignUpRequest(
        String email,
        String password,
        String nickname
) {
}
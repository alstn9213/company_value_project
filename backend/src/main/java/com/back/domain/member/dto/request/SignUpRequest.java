package com.back.domain.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "이메일 형식에 맞지 않습니다.")
        String email,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{8,20}$", message = "비밀번호는 영문, 숫자를 포함한 8~20자입니다.")
        String password,

        @NotBlank(message = "닉네임을 입력해주세요.")
        @Size(min = 2, max = 10, message = "닉네임은 2~10자입니다.")
        String nickname
) {
}
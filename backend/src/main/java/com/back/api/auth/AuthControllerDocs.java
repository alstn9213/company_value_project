package com.back.api.auth;

import com.back.domain.auth.dto.request.LoginRequest;
import com.back.domain.auth.dto.response.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth API", description = "인증 관련 API")
public interface AuthControllerDocs {

  @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하여 JWT 토큰을 발급받습니다.")
  ResponseEntity<TokenResponse> login(
          @Parameter(description = "로그인 요청 정보 (이메일, 비밀번호)", required = true)
          @RequestBody LoginRequest request
  );


}
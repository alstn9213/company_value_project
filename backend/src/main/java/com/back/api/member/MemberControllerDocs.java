package com.back.api.member;

import com.back.domain.member.dto.request.SignUpRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Member API", description = "회원가입 API")
public interface MemberControllerDocs {

  @Operation(summary = "회원가입", description = "새로운 회원을 등록합니다.")
  ResponseEntity<Void> signup(
          @Parameter(description = "회원가입 요청 정보 (이메일, 비밀번호, 닉네임)", required = true)
          @RequestBody SignUpRequest request
  );

}

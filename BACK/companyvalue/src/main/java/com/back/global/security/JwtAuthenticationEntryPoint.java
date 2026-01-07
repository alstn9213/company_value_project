package com.back.global.security;

import com.back.global.error.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
    // Filter에서 담아둔 예외 코드를 가져옵니다.
    ErrorCode errorCode = (ErrorCode) request.getAttribute("exception");

    if (errorCode == null) errorCode = ErrorCode.LOGIN_REQUIRED; // 기본 에러

    setResponse(response, errorCode);
  }

  // 한글 깨짐 방지 및 JSON 응답 설정 헬퍼
  private void setResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    // ErrorResponse 객체 생성 (기존에 정의된 클래스 활용)
    // 여기서는 간단히 Map이나 문자열로 예시를 들지만, 실제로는 ErrorResponse DTO를 쓰세요.
    String jsonResponse = objectMapper.writeValueAsString(
            java.util.Map.of(
                    "status", errorCode.getStatus().value(),
                    "code", errorCode.getCode(),
                    "message", errorCode.getMessage()
            )
    );

    response.getWriter().write(jsonResponse);
  }
}

package com.back.global.security;

import com.back.global.error.ErrorCode;
import com.back.global.error.ErrorResponse;
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
  // Spring이 관리하는 ObjectMapper 주입 (LocalDateTime 직렬화 설정 등 공유)
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
    // Filter에서 담아둔 예외 코드를 가져온다.
    ErrorCode errorCode = (ErrorCode) request.getAttribute("exception");

    if (errorCode == null) errorCode = ErrorCode.LOGIN_REQUIRED; // 기본 에러

    setResponse(response, errorCode);
  }

  // 한글 깨짐 방지 및 JSON 응답 설정 헬퍼
  private void setResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");

    // HTTP Status Code 설정 (JSON 바디에는 포함하지 않고 헤더에만 설정)
    response.setStatus(errorCode.getStatus().value());

    ErrorResponse errorResponse = ErrorResponse.of(errorCode);

    // 주입받은 ObjectMapper로 직렬화
    String jsonResponse = objectMapper.writeValueAsString(errorResponse);

    response.getWriter().write(jsonResponse);
  }
}

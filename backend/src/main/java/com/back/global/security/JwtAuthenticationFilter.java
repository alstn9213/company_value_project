package com.back.global.security;

import com.back.global.error.exception.BusinessException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  public static final String AUTHORIZATION_HEADER = "Authorization";
  public static final String BEARER_PREFIX = "Bearer ";
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  protected void doFilterInternal(
          @NonNull HttpServletRequest request,
          @NonNull HttpServletResponse response,
          @NonNull FilterChain filterChain
  ) throws ServletException, IOException {
    // Request Header 에서 토큰 추출
    String token = resolveToken(request);
    try {
      // 토큰 유효성 검사
      if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
        // 유효하면 Authentication 객체를 가져옴
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        // 객체를 SecurityContext에 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (BusinessException e) {
      log.error("JWT 유효성 검증 실패: code=[{}], msg=[{}]",
              e.getErrorCode().getCode(),
              e.getErrorCode().getMessage());
      request.setAttribute("exception", e.getErrorCode());
    }

    filterChain.doFilter(request, response);
  }

  // Request Header에서 토큰 정보 꺼내오는 헬퍼
  private String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
      return bearerToken.substring(7);
    }
    return null;
  }
}

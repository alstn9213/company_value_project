package com.back.domain.auth.service;

import com.back.domain.auth.dto.request.LoginRequest;
import com.back.domain.auth.dto.response.TokenResponse;
import com.back.domain.member.entity.Member;
import com.back.domain.member.repository.MemberRepository;
import com.back.global.error.ErrorCode;
import com.back.global.error.exception.BusinessException;
import com.back.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

  private final MemberRepository memberRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final AuthenticationManagerBuilder authenticationManagerBuilder;

  public TokenResponse login(LoginRequest request) {
    // ID/PW를 기반으로 AuthenticationToken 생성
    UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(request.email(), request.password());

    // 실제 검증
    Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

    // 인증 정보를 기반으로 JWT 토큰 생성
    Member member = memberRepository.findByEmail(request.email())
            .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

    String accessToken = jwtTokenProvider.createToken(authentication, member.getNickname());

    long expiresIn = jwtTokenProvider.getTokenValidityInMilliseconds();

    return TokenResponse.of(
            accessToken,
            expiresIn,
            member.getNickname()
    );
  }
}

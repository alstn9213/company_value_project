package com.back.global.security;

import com.back.global.error.ErrorCode;
import com.back.global.error.exception.BusinessException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Component
public class JwtTokenProvider {
  private static final String AUTHORITIES_KEY = "auth";

  @Value("${jwt.secret}")
  private String secretKey;

  @Value("${jwt.expiration}")
  private long tokenValidityInMilliseconds;

  private Key key;

  @PostConstruct
  public void init() {
    // Base64 인코딩된 키가 아니라면 getBytes()를 사용해야 안전합니다.
    // 사용자가 properties에 입력한 긴 문자열을 그대로 바이트로 변환하여 키로 사용합니다.
    byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
    this.key = Keys.hmacShaKeyFor(keyBytes);
  }

  // 토큰 생성
  public String createToken(Authentication authentication, String nickname) {
    String authorities = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

    long now = (new Date()).getTime();
    Date validity = new Date(now + this.tokenValidityInMilliseconds);

    return Jwts.builder()
            .setSubject(authentication.getName()) // email
            .claim(AUTHORITIES_KEY, authorities)
            .claim("nickname", nickname) // 닉네임
            .setIssuedAt(new Date())
            .setExpiration(validity)
            .signWith(key)
            .compact();
  }

  // 토큰에서 인증 정보 조회
  public Authentication getAuthentication(String token) {
    Claims claims = parseClaims(token);

    if (claims.get(AUTHORITIES_KEY) == null) {
      throw new BusinessException(ErrorCode.TOKEN_CLAIMS_EMPTY);
    }

    Collection<? extends  GrantedAuthority> authorities =
            Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                    .map(SimpleGrantedAuthority::new)
                    .toList();

    UserDetails principal = new User(claims.getSubject(), "", authorities);
    return new UsernamePasswordAuthenticationToken(principal, "", authorities);
  }

  // 토큰 유효성 검증
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
      throw new BusinessException(ErrorCode.TOKEN_SIGNATURE_INVALID);
    } catch (ExpiredJwtException e) {
      throw new BusinessException(ErrorCode.EXPIRED_TOKEN);
    } catch (UnsupportedJwtException e) {
      throw new BusinessException(ErrorCode.UNSUPPORTED_TOKEN);
    } catch (IllegalArgumentException e) {
      throw new BusinessException(ErrorCode.INVALID_TOKEN);
    }
  }

  // --- 헬퍼 메서드 ---
  private Claims parseClaims(String accessToken) {
    try {
      return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
    } catch (ExpiredJwtException e) {
      return e.getClaims();
    }
  }
}

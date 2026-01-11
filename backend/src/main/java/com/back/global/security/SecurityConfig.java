package com.back.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
            .csrf(AbstractHttpConfigurer::disable) // REST API 이므로 CSRF 보안 미사용
//                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 연결 -> 리액트와 스프링부트를 하나로 합쳐서 배포해, cors 설정이 필요없어짐
            .exceptionHandling(exception -> exception
                    .authenticationEntryPoint(jwtAuthenticationEntryPoint)) // 예외 처리 설정: 인증 실패 시 401 핸들러 호출
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // JWT 이므로 세션 미사용
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // OPTIONS 메서드는 인증 없이 모두 허용
                    .requestMatchers("/", "/index.html", "/assets/**", "/favicon.ico", "/*.ico", "/vite.svg").permitAll() // 정적 리소스(React 파일) 접근 허용
                    .requestMatchers("/auth/**", "/test/**", "/error").permitAll() // 로그인/회원가입 등 인증 없이 접근 가능한 API
                    .requestMatchers(HttpMethod.GET, "/api/macro/**", "/api/companies/**", "/api/scores/**").permitAll() // 공개 데이터 조회 API 허용
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**").permitAll() // swagger 관련 허용
                    .anyRequest().authenticated() // 나머지는 인증 필요
            )
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(List.of("*"));
    // 허용할 HTTP 메서드
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

    // 허용할 헤더
    configuration.setAllowedHeaders(List.of("*"));

    // 인증 정보(토큰 등)를 포함한 요청을 허용하려면 true
    configuration.setAllowCredentials(true);

    // 브라우저가 Authorization 헤더에 접근할 수 있도록 노출
    configuration.addExposedHeader("Authorization");

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}

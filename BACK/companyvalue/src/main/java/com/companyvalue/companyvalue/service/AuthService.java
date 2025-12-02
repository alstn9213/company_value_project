package com.companyvalue.companyvalue.service;

import com.companyvalue.companyvalue.domain.Member;
import com.companyvalue.companyvalue.domain.Role;
import com.companyvalue.companyvalue.domain.repository.MemberRepository;
import com.companyvalue.companyvalue.dto.AuthDto;
import com.companyvalue.companyvalue.global.error.ErrorCode;
import com.companyvalue.companyvalue.global.error.exception.BusinessException;
import com.companyvalue.companyvalue.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Transactional
    public  void signup(AuthDto.SignUpRequest request) {
        if(memberRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.EMAIL_DUPLICATION);
        }

        Member member = Member.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .role(Role.USER)
                .build();

        memberRepository.save(member);
    }

    @Transactional
    public AuthDto.TokenResponse login(AuthDto.LoginRequest request) {
        // 1. ID/PW를 기반으로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.email(), request.password());

        // 2. 실제 검증 (사용자 비밀번호 체크)
        // authenticate 메서드가 실행될 때 CustomUserDetailsService 의 loadUserByUsername 메서드가 실행됨
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        String accessToken = jwtTokenProvider.createToken(authentication, member.getNickname());

        return new AuthDto.TokenResponse(
                accessToken,
                "Bearer",
                1800000L, // 30분
                member.getNickname()
        );
    }
}

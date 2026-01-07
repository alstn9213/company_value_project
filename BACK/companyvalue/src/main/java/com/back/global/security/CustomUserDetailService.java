package com.back.global.security;

import com.back.domain.member.entity.Member;
import com.back.domain.member.repository.MemberRepository;
import com.back.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

  private final MemberRepository memberRepository;

  // AuthService의 login 메서드 안의 authenticate()가 호출되면
  // Security가 UserDetailsService의 구현체를 찾아
  // loadUserByUsername을 실행해서 DB의 회원 정보를 꺼내온다.
  @Override
  public UserDetails loadUserByUsername(String email) {
    return memberRepository.findByEmail(email)
            .map(this::createUserDetails)
            .orElseThrow(() -> new UsernameNotFoundException(ErrorCode.MEMBER_NOT_FOUND.getMessage()));
  }

  // DB의 Member를 Security의 UserDetails로 변환하는 헬퍼
  private UserDetails createUserDetails(Member member) {
    GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(member.getRole().getKey());

    return new User(
            String.valueOf(member.getId()), // Principal로 ID 저장
            member.getPassword(),
            Collections.singleton(grantedAuthority)
    );
  }

}

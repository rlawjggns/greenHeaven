package com.greenheaven.backend.security;

import com.greenheaven.backend.domain.Member;
import com.greenheaven.backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service // 비즈니스 로직이나 인증, 데이터 처리와 같은 핵심 로직을 담당한다
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService { // UserDetailsService 구현, Spring Security는 인증 과정에서 UserDetailService를 사용하여 사용자 정보를 로드
    private final MemberRepository memberRepository;

    // Spring Security는 로그인 시, AuthenticationManager를 통해 사용자의 자격증명을 검증하는데, 이때 이 메서드를 호출하여, 입력한 이메일에 해당하는 사용자 정보를 가져옴
    // 반환된 UserPrincipal에는 암호화된 비밀번호와 사용자의 권한등이 포함되어 있어서, Spring Security가 실제 입력된 비밀번호와 비교하여 인증 여부를 결정
    @Override // loadUserByUsername 메서드 구현, 사용자가 로그인 할 떄 입력한 이메일을 파라미터로 받아, 해당 이메일에 대한 사용자 정보를 반환할 것
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException { 
        log.info("이메일을 통해 유저를 찾는 중: {}", email);
        return memberRepository.findByEmail(email)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다. 이메일 : " + email)); // 해당 이메일로 유저를 찾지 못하면, 예외를 던짐
    }

    private UserDetails createUserDetails(Member member) {
        return new CustomUserDetails(member);
    }
}

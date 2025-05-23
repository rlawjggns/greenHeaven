package com.greenheaven.greenheaven_app.security;

import com.greenheaven.greenheaven_app.domain.Member;
import com.greenheaven.greenheaven_app.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다. 이메일 : " + email)); // 해당 이메일로 유저를 찾지 못하면, 예외를 던짐

        return new UserPrincipal(member); // 조회한 User 엔티티를 기반으로 UserPrincipal 객체를 생성해 반환, UserPrincipal은 UserDetail 인터페이스를 구현한 클래스이며, 시큐리티가 사용자 인증 및 권한 부여를 위해 필요한 정보를 제공해줌
    }
}

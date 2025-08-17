package com.greenheaven.backend.config;

import com.greenheaven.backend.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@RequiredArgsConstructor
@EnableWebSecurity // Spring Security 기능 활성화
public class SecurityConfig {
    private final CustomUserDetailsService customUserDetailsService; // 커스텀 UserDetailService를 의존성 주입, @Service 어노테이션 덕분에 Sprinng Security가 UserDetailsService로써 이를 자동으로 찾아 사용
    // CustomUserDetailService를 제거하면 Spring Security가 사용자 인증을 처리할 때 UserDetailsService를 구현한 클래스를 찾을 수 없게 되어 인증 과정에 문제가 발생

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { // HTTP 요청 보안을 구성하기 위한 빌더 객체를 인자로 받아 설정을 시작 
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화(개발 중)
                .authorizeHttpRequests(auth -> auth // HTTP 요청에 대한 접근 제어 설정
                        .requestMatchers("/", "/member/login", "/member/signup", "/member/password/reset", "/member/password/reset/confirm","/error","/css/**", "/js/**", "/images/**").permitAll() // 괄호 안의 URL 패턴(홈, 로그인, 회원가입, 에러 페이지, 정적 리소스 경로 등)은 인증 없이 접근 가능하게 허용
                        .anyRequest().authenticated() // 위의 패턴에 해당하지 않는 모든 요청은 인증을 받아야 접근 할 수 있도록 설정
                )
                .formLogin(login -> login // 폼 기반 로그인 설정
                        .loginPage("/member/login") // 사용자 정의 로그인 페이지 URL 지정 -> Thymeleaf에서 사용하는 URL과 맞춤
                        .loginProcessingUrl("/member/loginProcess") // 로그인 폼의 action URL 지정, 시큐리티가 이 URL을 통해 로그인 인증을 처리 -> 실제 로그인 처리를 담당하는 URL
                        .failureUrl("/member/login?error=true")  // 로그인 실패 시, 오류 메시지 처리를 위해 지정된 URL로 리다이렉트
                        .defaultSuccessUrl("/", true) // 로그인 성공 시, 기본으로 이동할 URL. 두번째 인자는 항상 이 URL로 리다이렉트 하도록 강제함.
                        .permitAll() // 로그인 페이지 접근에 대해서는 인증 없이 접근 허용
                )
                .logout(logout -> logout // 로그아웃 관련 설정
                        .logoutUrl("/member/logout") // Thymeleaf와 일치하는 로그아웃 URL
                        .logoutSuccessUrl("/member/login?logout=true") // 로그아웃 성공 시, 리다이렉트할 URL 지정 -> 쿼리 파라미터를 사용하여 Thymeleaf에서 param.logout을 통해 로그아웃 완료 메시지 표시
                        .invalidateHttpSession(true) // 로그아웃 시 세션 무효화
                        .deleteCookies("JSESSIONID") // 인증에 사용된 쿠키(JSESSIONID)를 삭제하여 남은 인증정보 제거
                        .permitAll() // 로그아웃 URL 접근에 대해서는 인증 없이 접근 허용
                )
                .rememberMe(rememberMe -> rememberMe
                        .key("mySecretKey") // 쿠키 암호화 키
                        .tokenValiditySeconds(60 * 60 * 24) // 1일 유지
                        .userDetailsService(customUserDetailsService) // 반드시 UserDetailService 필요
                );
        return http.build(); // 위의 설정을 기반으로 SecurityFilterChian 객체를 빌드하여 리턴
    }

    @Bean // AuthenticationManager = 인증 요청을 처리하는 핵심 컴포넌트
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception { 
        return authenticationConfiguration.getAuthenticationManager(); // AuthenticationConfiguration은 스프링 시큐리티가 내부적으로 관리하는 인증 관련 설정정보를 담고있으며, 이를 통해 AuthenticationManager를 생성
    }

    @Bean // PasswordEncoder = 사용자의 비밀번호를 암호화(인코딩)하고, 로그인 시 입력한 비밀번호와 암호화된 비밀번호를 비교하는 역할을 한다.
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt 해시 함수를 사용하는 구현체를 반환하는데, 이 BCrypt는 안전하게 비밀번호를 저장가능한 암호화 방식이며, 추후 회원가입이 비밀번호를 암호화 하거나, 로그인 시 입력한 비밀번호를 비교할 때 활용가능
    }
}

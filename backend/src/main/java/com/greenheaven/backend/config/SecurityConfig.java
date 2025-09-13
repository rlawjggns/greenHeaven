package com.greenheaven.backend.config;

import com.greenheaven.backend.security.JwtAuthenticationFilter;
import com.greenheaven.backend.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@RequiredArgsConstructor
@EnableWebSecurity // Spring Security 기능 활성화
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { // HTTP 요청 보안을 구성하기 위한 빌더 객체를 인자로 받아 설정을 시작 
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화(개발 중)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth // HTTP 요청에 대한 접근 제어 설정
                        .requestMatchers(
                                "/api/member/signup",
                                "/api/member/signin",
                                "/api/member/check-auth",
                                "/api/member/password-reset/**",
                                "/api/member/email/**")
                        .permitAll() // 괄호 안의 URL 패턴은 인증 없이 접근 가능하게 허용
                        .requestMatchers("/api/admin/**").hasRole("ADMIN") // ADMIN 관리자
                        .anyRequest().authenticated() // 위의 패턴에 해당하지 않는 모든 요청은 인증을 받아야 접근 할 수 있도록 설정
                )
                .headers(headers -> headers.frameOptions(frameoptions -> frameoptions.sameOrigin()))
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    // 인증 안 됨 → 401
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    // 인증은 됐는데 권한 없음 → 403
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
                })
            );
        return http.build(); // 위의 설정을 기반으로 SecurityFilterChian 객체를 빌드하여 리턴
    }

    @Bean // PasswordEncoder = 사용자의 비밀번호를 암호화(인코딩)하고, 로그인 시 입력한 비밀번호와 암호화된 비밀번호를 비교하는 역할을 한다.
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt 해시 함수를 사용하는 구현체를 반환하는데, 이 BCrypt는 안전하게 비밀번호를 저장가능한 암호화 방식이며, 추후 회원가입이 비밀번호를 암호화 하거나, 로그인 시 입력한 비밀번호를 비교할 때 활용가능
    }
}

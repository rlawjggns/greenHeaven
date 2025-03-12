package com.greenheaven.greenheaven_app;

import com.greenheaven.greenheaven_app.domain.dto.UserProfileDto;
import com.greenheaven.greenheaven_app.domain.dto.UserSignUpDto;
import com.greenheaven.greenheaven_app.domain.entity.User;
import com.greenheaven.greenheaven_app.repository.UserRepository;
import com.greenheaven.greenheaven_app.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("유저")
class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        // 가짜 사용자 정보 생성
        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername("test@example.com")
                .password("password123")
                .roles("USER")
                .build();

        // 테스트 SecurityContext 생성
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
        );

        // SecurityContextHolder에 설정
        SecurityContextHolder.setContext(securityContext);

        // 유저 회원가입
        UserSignUpDto request = UserSignUpDto.builder()
                .email("test@example.com")
                .password("password123")
                .confirmPassword("password123")
                .name("TestUser").build();

        userService.SignUp(request);
    }
    
    @DisplayName("이메일 중복 체크")
    @Test
    void checkEmail() {
        //given
        String email = "test@example.com"; // 테스트할 이메일

        //when
        Optional<User> findUser = userService.checkEmail(email); // 이메일로 만든 유저가 있는지 확인

        //then
        assertThat(findUser).isPresent(); // 유저가 존재하는지 확인
    }
    
    @DisplayName("회원가입")
    @Test
    void signUp() {
        //given
        UserSignUpDto request = UserSignUpDto.builder() // 테스트할 회원가입 dto 생성
                .email("test2@example.com")
                .password("password123")
                .confirmPassword("password123")
                .name("TestUser2").build();

        //when
        userService.SignUp(request); // 회원가입 서비스 로직

        //then
        assertThat(userRepository.findByEmail(request.getEmail())).isPresent(); // 유저가 회원가입 되었는지 확인
    }
    
    @DisplayName("이메일로 비밀번호 찾기")
    @Test
    void findPassword() {
        //given
        String email = "test@example.com"; // 테스트할 이메일

        //when
        String findPassword = userService.findPassword(email); // 이메일 -> 비밀번호 조회 서비스 로직

        //then
        assertThat(passwordEncoder.matches("password123", findPassword)).isTrue(); // 예상하는 비밀번호 값과 같은지 확인 
    }

    @DisplayName("이메일로 임시 비밀번호 전송")
    @Test
    void sendPasswordByEmail() {
        //given
        String email =  "test@example.com"; // 테스트할 이메일

        //when
        userService.sendPasswordByEmail(email); // 임시 비밀번호 전송 서비스 로직

        //then

    }

    @DisplayName("프로필 조회")
    @Test
    void getProfile() {
        //given
        String email =  "test@example.com"; // 테스트할 이메일

        //when
        UserProfileDto profile = userService.getProfile(); // 프로필 조회 서비스 로직

        //then
        assertThat(profile.getName()).isEqualTo("TestUser"); // 예상하는 이름과 같은지 확인
    }

    @DisplayName("프로필 수정")
    @Test
    void updateProfile() {
        //given
        UserProfileDto dto = UserProfileDto.builder() // 테스트할 프로필 수정 dto 생성
                .name("TestUserUpdate")
                .oldPassword("password123")
                .newPassword("password321")
                .confirmNewPassword("password321")
                .build();

        String email =  "test@example.com"; // 테스트할 이메일

        //when
        userService.updateProfile(dto); // 프로필 수정 서비스 로직

        //then
        User user = userRepository.findByEmail("test@example.com").get(); // 유저 객체 바로 꺼내기

        assertThat(user.getName()).isEqualTo("TestUserUpdate"); // 이름이 예상하는 값과 같은지 확인
        assertThat(passwordEncoder.matches("password321", user.getPassword())).isTrue(); // 비밀번호도 예상하는 값과 같은지 확인
    }

    @DisplayName("탈퇴")
    @Test
    void quit() {
        //given
        String email =  "test@example.com"; // 테스트할 이메일

        //when
        userService.quit(); // 탈퇴 서비스 로직

        //then
        assertThat(userRepository.findByEmail(email)).isEmpty(); // 이메일로 유저를 조회했을때 없는지 확인
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull(); // SecurityContext가 비워졌는지 확인
    }
}
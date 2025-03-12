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

        // SecurityContext 생성
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
        );

        // SecurityContextHolder에 설정
        SecurityContextHolder.setContext(securityContext);

        // 유저 회원가입
        UserSignUpDto request = new UserSignUpDto();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setConfirmPassword("password123");
        request.setName("TestUser");

        userService.SignUp(request);
    }
    
    @DisplayName("이메일 중복 체크")
    @Test
    void checkEmail() {
        //given
        String email = "test@example.com";

        //when
        Optional<User> findUser = userService.checkEmail(email);

        //then
        assertThat(findUser).isPresent();
    }
    
    @DisplayName("회원가입")
    @Test
    void signUp() {
        //given
        UserSignUpDto request = new UserSignUpDto();
        request.setEmail("test2@example.com");
        request.setPassword("password123");
        request.setConfirmPassword("password123");
        request.setName("TestUser2");

        //when
        userService.SignUp(request);

        //then
        assertThat(userRepository.findByEmail(request.getEmail())).isPresent();
    }
    
    @DisplayName("이메일로 비밀번호 찾기")
    @Test
    void findPassword() {
        //given
        String email = "test@example.com";

        //when
        String findPassword = userService.findPassword(email);

        //then
        assertThat(passwordEncoder.matches("password123", findPassword)).isTrue();
    }

    @DisplayName("이메일로 임시 비밀번호 전송")
    @Test
    void sendPasswordByEmail() {
        //given
        String email =  "test@example.com";

        //when
        userService.sendPasswordByEmail(email);

        //then

    }

    @DisplayName("프로필 조회")
    @Test
    void getProfile() {
        //given
        String email =  "test@example.com";

        //when
        UserProfileDto profile = userService.getProfile();

        //then
        assertThat(profile.getName()).isEqualTo("TestUser");
    }

    @DisplayName("프로필 수정")
    @Test
    void updateProfile() {
        //given
        UserProfileDto dto = UserProfileDto.builder()
                .name("TestUserUpdate")
                .oldPassword("password123")
                .newPassword("password321")
                .confirmNewPassword("password321")
                .build();

        String email =  "test@example.com";

        //when
        userService.updateProfile(dto);

        //then
        User user = userRepository.findByEmail("test@example.com").get();

        assertThat(user.getName()).isEqualTo("TestUserUpdate");
        assertThat(passwordEncoder.matches("password321", user.getPassword())).isTrue();
    }

    @DisplayName("탈퇴")
    @Test
    void quit() {
        //given
        String email =  "test@example.com";

        //when
        userService.quit();

        //then
        assertThat(userRepository.findByEmail(email)).isEmpty();

        // SecurityContext가 비워졌는지 확인
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
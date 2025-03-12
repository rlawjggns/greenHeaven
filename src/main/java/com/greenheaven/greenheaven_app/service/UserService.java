package com.greenheaven.greenheaven_app.service;

import com.greenheaven.greenheaven_app.domain.dto.UserProfileDto;
import com.greenheaven.greenheaven_app.domain.dto.UserSignUpDto;
import com.greenheaven.greenheaven_app.domain.entity.Subscription;
import com.greenheaven.greenheaven_app.domain.entity.User;
import com.greenheaven.greenheaven_app.exception.*;
import com.greenheaven.greenheaven_app.repository.SubscriptionRepository;
import com.greenheaven.greenheaven_app.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    /**
     * 이메일 중복 확인 서비스 로직
     * @param email 중복 여부를 확인할 이메일
     * @return 이메일이 이미 등록된 유저가 있으면 유저를 포함한 Optional, 없으면 비어있는 Optional 반환
     */
    public Optional<User> checkEmail(String email) {
        // 이메일로 가입된 유저가 있는지 조회한 결과 반환
        return userRepository.findByEmail(email);
    }

    /**
     * 회원가입 서비스 로직
     * @param request 회원가입에 필요한 정보를 담은 DTO
     */
    public void SignUp(UserSignUpDto request) {
        // 이미 가입된 이메일인 경우 예외 처리
        if (checkEmail(request.getEmail()).isPresent()) {
            throw new EmailExistException("이미 가입된 이메일입니다.");
        }

        // 비밀번호 확인과 비밀번호에 입력된 값이 다를 시 예외 처리
        if (!request.getConfirmPassword().equals(request.getPassword())) {
            throw new OldPasswordMismatchException("비밀번호가 일치하지 않습니다.");
        }

        // 유저의 구독 개체 생성
        Subscription subscription = new Subscription(
                LocalDateTime.of(9999,9,9,23,59)
        );
        subscriptionRepository.save(subscription);

        // 유저 생성
        User user = new User(
                request.getName(),
                passwordEncoder.encode(request.getPassword()),
                request.getEmail(),
                subscription
        );

        // 유저 저장
        userRepository.save(user);
    }

    /**
     * 비밀번호 검색 서비스 로직
     * @param email 비밀번호를 찾을 유저의 이메일
     * @return 이메일을 통해 찾은 유저의 비밀번호 반환
     * @throws NoSuchElementException 이메일로 유저를 찾지 못할 경우 예외 발생
     */
    public String findPassword(String email) {
        // 이메일로 유저 찾기
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));

        // 찾은 유저의 비밀번호 반환
        return user.getPassword();
    }

    /**
     * 임시 비밀번호 전송 서비스 로직
     * @param email 임시 비밀번호를 보낼 유저의 이메일
     * @throws NoSuchElementException 이메일로 유저를 찾지 못할 경우 예외 발생
     */
    public void sendPasswordByEmail(String email) {
        // 이메일로 유저 찾기
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));

        // 0~ 9 사이의 8자리 랜덤 비밀번호 생성
        Random random = new Random();
        StringBuilder tempPassword = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            tempPassword.append(random.nextInt(10));
        }

        // 유저의 비밀번호 변경
        user.updatePassword(passwordEncoder.encode(tempPassword));

        // 메일의 제목, 내용 설정
        String subject = "GreenHeaven 계정 비밀번호 안내"; 
        String content = "<p>안녕하세요, GreenHeaven입니다.</p>" + 
                "<p>요청하신 계정의 임시 비밀번호는 아래와 같습니다:</p>" +
                "<p><strong>임시 비밀번호: " + tempPassword + "</strong></p>" +
                "<p>보안을 위해 바로 비밀번호를 변경하는 것을 추천드립니다.</p>";

        try {
            // 이메일 메시지 객체 MimeMessage 생성 후,
            MimeMessage message = mailSender.createMimeMessage();

            // 이메일을 쉽게 설정하기 위한 헬퍼 객체 MimeMessageHelper를 사용해 이메일 설정
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            // 이메일 수신자 설정
            helper.setTo(email); // 보낼 이메일
            
            // 이메일 제목 설정
            helper.setSubject(subject); // 제목
            
            // 이메일 내용 설정, HTML 형식으로 전송
            helper.setText(content, true); // 내용
            
            // 이메일 전송
            mailSender.send(message);
        } catch (MessagingException e) {
            
            // 이메일 전송 실패 시 예외 처리
            throw new RuntimeException("이메일 전송 실패", e);
        }
    }

    /**
     * 프로필 조회 서비스 로직
     * @return 이메일을 통해 찾은 유저의 프로필 정보를 DTO에 담아 반환
     * @throws NoSuchElementException 이메일로 유저를 찾지 못할 경우 예외 발생
     */
    public UserProfileDto getProfile() {
        // 현재 인증된 사용자의 인증 정보 가져오기
        String email = getAuthenticatedUserEmail();

        // 이메일로 유저 찾기
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));

        // DTO 반환
        return UserProfileDto.builder()
                .name(user.getName())
                .build();
    }

    /**
     * 프로필 수정 서비스 로직
     * @param request 유저의 프로필 정보를 담은 DTO
     * @throws NoSuchElementException 이메일로 유저를 찾지 못할 경우 예외 발생
     */
    public void updateProfile(UserProfileDto request) {
        // 현재 인증된 사용자의 인증 정보 가져오기
        String email = getAuthenticatedUserEmail();

        // 프로필 수정필드 검증 메서드
        validateProfileUpdate(request, email);

        // 이메일로 유저 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));

        // 기존 이름과 같지 않으면 이름 수정
        if (!request.getName().equals(user.getName())) {
            user.updateName(request.getName());
        }

        // 새로운 비밀번호 값이 비지 않았다면 비밀번호 수정
        if (!request.getNewPassword().isBlank()) {
            user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        }
    }

    /**
     * 프로필 수정필드 검증 메서드
     * @param request 유저의 프로필 정보를 담은 DTO
     * @param email 프로필을 수정할 유저의 이메일
     * @throws  IllegalArgumentException 필드 검증 중 오류 발생시 예외 발생
     */
    private void validateProfileUpdate(UserProfileDto request, String email) {
        // 이메일로 유저의 비밀번호 찾기
        String storedPassword = findPassword(email);

        // 비밀번호와 일치하지 않을 시 예외 처리
        if(!passwordEncoder.matches(request.getOldPassword(), storedPassword)) { 
            throw new OldPasswordMismatchException("기본 비밀번호가 일치하지 않습니다."); 
        }

        // 새로운 비밀번호는 입력되었지만, 최소 최대 자리수를 만족하지 않을 시 예외 처리
        if (!request.getNewPassword().isBlank() &&
                (request.getNewPassword().length() < 8 || request.getNewPassword().length() > 20)) { 
            throw new NewPasswordLengthException("최소 8글자 이상, 최대 20자 이하");
        }

        // 새로운 비밀번호는 입력되었지만, 새로운 비밀번호 확인이 비어있을 시 예외 처리
        if(!request.getNewPassword().isBlank() && request.getConfirmNewPassword().isBlank()) { 
            throw new NewPasswordBlankException("비밀번호를 한번 더 입력하세요.");
        }

        // 새로운 비밀번호는 입력되었지만, 새로운 비밀번호 확인과 일치하지 않을 시 예외 처리
        if (!request.getNewPassword().isBlank() && !request.getNewPassword().equals(request.getConfirmNewPassword())) { 
            throw new NewPasswordMismatchException("비밀번호가 일치하지 않습니다.");
        }
    }

    /**
     * 회원탈퇴 서비스 로직
     * @throws NoSuchElementException 이메일로 유저를 찾지 못할 경우 예외 발생
     */
    public void quit(HttpServletRequest request, HttpServletResponse response) {
        // 현재 인증된 사용자의 인증 정보 가져오기
        String email = getAuthenticatedUserEmail();

        // 이메일로 유저 찾기
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));

        // 찾은 유저 삭제
        userRepository.delete(user);

        // 세션 무효화 및 로그아웃 처리
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        new SecurityContextLogoutHandler().logout(request, response, authentication);

        // JSESSIONID 쿠키 삭제
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setMaxAge(0); // 즉시 만료
        cookie.setPath("/"); // 애플리케이션 전체에 적용된 쿠키 삭제
        response.addCookie(cookie);
    }

    /**
     * 회원탈퇴 서비스 로직(테스트용으로 사용할 오버로딩 메서드)
     * @throws NoSuchElementException 이메일로 유저를 찾지 못할 경우 예외 발생
     */
    public void quit() {
        // 현재 인증된 사용자의 인증 정보 가져오기
        String email = getAuthenticatedUserEmail();

        // 이메일로 유저 찾기
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));

        // 찾은 유저 삭제
        userRepository.delete(user);

        // SecurityContext 초기화
        SecurityContextHolder.clearContext();
    }

    /**
     * 인증된 유저의 이메일을 가져오는 메서드
     * @return 유저의 이메일
     */
    private static String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}

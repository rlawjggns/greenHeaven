package com.greenheaven.greenheaven_app.service;

import com.greenheaven.greenheaven_app.domain.dto.UserProfileDto;
import com.greenheaven.greenheaven_app.domain.dto.UserSignUpDto;
import com.greenheaven.greenheaven_app.domain.entity.Subscription;
import com.greenheaven.greenheaven_app.domain.entity.User;
import com.greenheaven.greenheaven_app.repository.SubscriptionRepository;
import com.greenheaven.greenheaven_app.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
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
        return userRepository.findByEmail(email); // 이메일로 가입된 유저가 있는지 찾고 결과 반환
    }

    /**
     * 회원가입 서비스 로직
     * @param request 회원가입에 필요한 정보를 담은 DTO
     */
    public void SignUp(UserSignUpDto request) {

        Subscription subscription = new Subscription( // 유저를 위한 구독 생성하기
                LocalDateTime.of(9999,9,9,23,59)
        );
        subscriptionRepository.save(subscription);

        User user = new User( // 유저 생성하기
                request.getName(),
                passwordEncoder.encode(request.getPassword()),
                request.getEmail(),
                subscription
        );
        userRepository.save(user); // 유저 저장하기
    }

    /**
     * 비밀번호 검색 서비스 로직
     * @param email 비밀번호를 찾을 유저의 이메일
     * @return 이메일을 통해 찾은 유저의 비밀번호 반환
     * @throws NoSuchElementException 이메일로 유저를 찾지 못할 경우 예외 발생
     */
    public String findPwd(String email) {
        User user = userRepository.findByEmail(email) // 이메일로 유저 찾기
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));
        return user.getPassword(); // 찾은 유저의 비밀번호 반환
    }

    /**
     * 임시 비밀번호 전송 서비스 로직
     * @param email 임시 비밀번호를 보낼 유저의 이메일
     * @throws NoSuchElementException 이메일로 유저를 찾지 못할 경우 예외 발생
     */
    public void sendPasswordByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));

        Random random = new Random();
        StringBuilder tempPassword = new StringBuilder(); 

        for (int i = 0; i < 8; i++) {
            tempPassword.append(random.nextInt(10)); // 0~ 9 사이의 8자리 랜덤 비밀번호 생성
        }
        user.updatePassword(passwordEncoder.encode(tempPassword)); // 유저의 비밀번호 변경

        String subject = "GreenHeaven 계정 비밀번호 안내"; // 메일의 제목
        String content = "<p>안녕하세요, GreenHeaven입니다.</p>" + // 메일읜 내용
                "<p>요청하신 계정의 임시 비밀번호는 아래와 같습니다:</p>" +
                "<p><strong>임시 비밀번호: " + tempPassword + "</strong></p>" +
                "<p>보안을 위해 바로 비밀번호를 변경하는 것을 추천드립니다.</p>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email); //
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 전송 실패", e);
        }
    }

    /**
     * 프로필 조회 서비스 로직
     * @param email 프로필 정보를 조회할 유저의 이메일
     * @return 이메일을 통해 찾은 유저의 프로필 정보를 DTO에 담아 반환
     * @throws NoSuchElementException 이메일로 유저를 찾지 못할 경우 예외 발생
     */
    public UserProfileDto getUserProfile(String email) {
        User user = userRepository.findByEmail(email) // 이메일로 유저 찾기
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));
        return UserProfileDto.builder() // dto 반환
                .name(user.getName())
                .build();
    }

    /**
     * 프로필 수정 서비스 로직
     * @param request 유저의 프로필 정보를 담은 DTO
     * @param email 프로필 정보를 수정할 유저의 이메일
     * @throws NoSuchElementException 이메일로 유저를 찾지 못할 경우 예외 발생
     */
    public void updateProfile(UserProfileDto request, String email) {
        User user = userRepository.findByEmail(email) // 이메일로 유저 찾기
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));

        if (!request.getName().equals(user.getName())) { //이름이 같지 않으면
            user.updateName(request.getName());
        }

        if (!request.getNewPassword().isBlank()) { // 새로운 패스워드 값이 비지 않았다면
            user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        }
    }

    /**
     * 회원탈퇴 서비스 로직
     * @param email 탈퇴할 유저의 이메일
     * @throws NoSuchElementException 이메일로 유저를 찾지 못할 경우 예외 발생
     */
    public void quit(String email) {
        User user = userRepository.findByEmail(email) // 이메일로 유저 찾기
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));

        userRepository.delete(user); // 유저 삭제
    }
}

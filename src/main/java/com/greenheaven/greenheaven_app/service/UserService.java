package com.greenheaven.greenheaven_app.service;

import com.greenheaven.greenheaven_app.domain.dto.UserSignUpDto;
import com.greenheaven.greenheaven_app.domain.entity.Subscription;
import com.greenheaven.greenheaven_app.domain.entity.User;
import com.greenheaven.greenheaven_app.repository.SubscriptionRepository;
import com.greenheaven.greenheaven_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> checkEmail(UserSignUpDto request) {
        return userRepository.findByEmail(request.getEmail()); // 이메일로 가입된 유저가 있는지 찾고 결과 반환
    }

    public void SignUp(UserSignUpDto request) {
        User user = new User( // 유저 생성하기
                request.getName(),
                passwordEncoder.encode(request.getPassword()),
                request.getEmail()
        );
        userRepository.save(user); // 유저 저장하기

        Subscription subscription = new Subscription( // 유저를 위한 구독 생성하기
                LocalDate.of(9999,9,9),
                user
        );
        subscriptionRepository.save(subscription); // 구독 저장하기
    }
}

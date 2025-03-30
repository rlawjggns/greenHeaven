package com.greenheaven.greenheaven_app.service;

import com.greenheaven.greenheaven_app.domain.entity.Notification;
import com.greenheaven.greenheaven_app.domain.entity.Member;
import com.greenheaven.greenheaven_app.repository.NotificationRepository;
import com.greenheaven.greenheaven_app.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    /**
     * 알림 10건 조회
     * @return 알림 객체 10건 반환
     * @throws NoSuchElementException 이메일로 유저를 찾지 못할 경우 예외 발생
     */
    public List<Notification> getNotificationListTen() {
        // 현재 인증된 사용자의 인증 정보 가져오기
        String email = MemberService.getAuthenticatedMemberEmail();

        // 이메일로 유저 찾기
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));

        return notificationRepository.findTop10ByMember(member);
    }
}

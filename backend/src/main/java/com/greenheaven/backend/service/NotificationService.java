package com.greenheaven.backend.service;

import com.greenheaven.backend.domain.Notification;
import com.greenheaven.backend.dto.NotificationListResponseDto;
import com.greenheaven.backend.repository.EmitterRepository;
import com.greenheaven.backend.repository.MemberRepository;
import com.greenheaven.backend.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;

    public List<NotificationListResponseDto> getNotifications() {
        // 현재 인증된 사용자의 인증 정보 가져오기
        String email = MemberService.getAuthenticatedMemberEmail();

        // 알림들 조회
        List<Notification> notifications = notificationRepository.findByReceiverEmail((email));

        //알림물 객체 -> response 객체로 스트림 이용해 변환 후 반환
        return notifications.stream()
                .map(n -> NotificationListResponseDto.builder()
                        .id(n.getId())
                        .content(n.getContent())
                        .type(n.getType().getKorName())
                        .createdDate(n.getCreatedDate())
                        .build()
                )
                .toList();
    }

    public List<NotificationListResponseDto> getNotificationsTen() {
        // 현재 인증된 사용자의 인증 정보 가져오기
        String email = MemberService.getAuthenticatedMemberEmail();

        // 알림들 10개 조회
        List<Notification> notifications = notificationRepository.findTop10ByReceiverEmailOrderByCreatedDateDesc(email);

        //알림물 객체 -> response 객체로 스트림 이용해 변환 후 반환
        return notifications.stream()
                .map(n -> NotificationListResponseDto.builder()
                        .id(n.getId())
                        .content(n.getContent())
                        .type(n.getType().getKorName())
                        .createdDate(n.getCreatedDate())
                        .build()
                )
                .toList();
    }

    public void deleteNotification(String notificationId) {
        notificationRepository.deleteById(UUID.fromString(notificationId));
    }

    // 연결 지속 시간 -> 1시간  -> 1시간 동안 아무 이벤트도 보내지 않으면 타임아웃
    // 중간에 알람이라도 하나 보내면 타임아웃이 갱신된다
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    public SseEmitter connect(String lastEventId) {
        // 매 연결마다 고유한 이벤트 아이디 생성
        // 이 값은 클라이언트가 수신한 마지막 이벤트를 기억할 수 있게 하기 위해 사용
        // 이 ID는 클라이언트가 자동 재연결 시 Last-Event-ID로 되돌려 보내주는 기준이 된다.
        String receiverEmail = MemberService.getAuthenticatedMemberEmail();
        String eventId = receiverEmail + "_" + System.currentTimeMillis();

        // ✅ 기존 연결이 있으면 먼저 종료
        emitterRepository.findAllEmitterStartWithEmail(receiverEmail)
                .forEach((key, emitter) -> {
                    emitter.complete(); // 기존 연결 종료
                    emitterRepository.deleteById(key); // 정리
                });

        // SseEmitter 인스턴스 생성 후 Map에 저장
        // 연결된 클라이언트 1명에 대한 SSE 스트림 객체
        // 이 emitter를 통해 메시지를 계속 밀어넣는다.
        // sendToClient()는 결국 이 emitter 인스턴스의 .send()를 호출한다
        SseEmitter emitter = emitterRepository.save(eventId, new SseEmitter(DEFAULT_TIMEOUT));

        // 클라이언트가 연결을 정상적으로 종료했다면, SseEmitter 인스턴스를 자동으로 삭제
        emitter.onCompletion(() -> {
            log.info("onCompletion 콜백");
            emitterRepository.deleteById(eventId);
        });

        // SseEmitter가 설정된 시간 안에 아무 이벤트도 전송하지 않았으면, SseEmitter 인스턴스를 자동으로 삭제
        emitter.onTimeout(() -> {
            log.info("onTimeout 콜백");
            emitterRepository.deleteById(eventId);
        });

        // 최초 연결 시 응답을 보내지 않으면 503 오류가 발생 -> 오류 방지를 위해 최초 연결시 더미 이벤트 생성 후 클라이언트에 전송
        sendToClient(eventId, emitter, "알림 서버 연결 성공. [receiverEmail=" + receiverEmail + "]", "dummy");

        // [재전송 로직]
        // 클라이언트가 마지막으로 받은 이벤트 ID(Last-Event-ID)를 서버에 보내면,
        // 그 이후에 서버에서 발생한 이벤트들만 필터링하여 재전송함.
        // 클라이언트와의 연결 끊긴 사이에 놓친 알림들을 복구하는 역할
        if(!lastEventId.isEmpty()) {
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithEmail(receiverEmail);
            events.entrySet().stream()
                    // a.compareTo(b) 가 음수 -> a 가 b 보다 작다는 뜻 -> lastEventId보다 큰 것들만 필터링
                    .filter(e -> lastEventId.compareTo(e.getKey()) < 0)
                    .forEach(e -> sendToClient(e.getKey(), emitter, e.getValue(), "notification"));
        }
        // 최초 연결 요청에 대해 서버가 SSE 스트림을 열어주면서 SseEmitter를 반환
        // 클라이언트는 이 반환된 SseEmitter를 통해 서버로부터 이벤트를 받게 된다.
        return emitter;
    }

    /**
     * 다른 서비스 클래스에서 이벤트 발생 시, 이 메서드를 호출하여 SSE 연결을 통해 보낼 알람을 준비한다.
     * @param notification 알람 객체(이 객체를 DB에 저장한 후 넘어올 것으로 기대)
     */
    public void send(Notification notification) {

        String eventId = notification.getReceiverEmail() + "_" + System.currentTimeMillis();
        log.info("[send] 이벤트 ID: {}, 수신자: {}", eventId, notification.getReceiverEmail());

        // 한 사용자가 여러 클라이언트(브라우저 탭, 모바일 앱 등)에서 접속할 수 있으므로, 사용자의 모든 SSE 연결(emitter)을 가져온다.
        Map<String, SseEmitter> sseEmiters = emitterRepository.findAllEmitterStartWithEmail(notification.getReceiverEmail());
        log.info("[send] 찾은 SSE emitters 개수: {}", sseEmiters.size());

        sseEmiters.forEach(
                (key, emitter) -> {
                    try {
                        // 전송 전 알림 데이터를 캐시에 저장하여 네트워크 장애 등으로 인한 이벤트 유실을 방지한다.
                        // 전송 시점에서 실패하여 현재 emitter가 삭제되어도, 클라이언트가 재접속할 때 Last-Event-ID를 이용해 캐시에서 못 받은 알림들을 다시 보낼 수 있다.
                        emitterRepository.saveEventCache(key, notification);
                        sendToClient(eventId, emitter, notification.getContent(), "notification");
                        log.info("알림 전송 성공 {}", notification.getContent());
                    } catch (Exception e) {
                        emitterRepository.deleteById(key); // 전송 중 오류 발생 시 해당 emitter는 삭제 -> 서버는 더 이상 그 연결을 통해 이벤트를 보내지 않는다
                        log.error("알림 전송 실패", e);
                    }
                }
        );

    }

    /**
     * 서버 측 이벤트를 클라이언트로 전송 메서드
     * @param eventId 이벤트 아이디
     * @param emitter SseEmitter 객체
     * @param object 데이터
     */
    private void sendToClient(String eventId, SseEmitter emitter, Object object, String eventName) {
        try {
            // SseEmitter의 send() 메서드를 통해 클라이언트에 이벤트 전송, 이름은 "connect"로 지정
            // 이벤트 ID와 데이터를 함께 전송하여 클라이언트가 수신 이벤트를 추적할 수 있게 한다.
            emitter.send(SseEmitter.event().name(eventName).id(eventId).data(object));
        } catch (IOException e) {
            emitterRepository.deleteById(eventId); // 예외 발생 시, 해당 emitter를 삭제하여 더 이상 이벤트를 보내지 않도록 한다
            throw new RuntimeException("알림 서버 연결 오류");
        }
    }
}

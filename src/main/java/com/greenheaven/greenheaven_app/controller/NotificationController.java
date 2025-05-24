package com.greenheaven.greenheaven_app.controller;

import com.greenheaven.greenheaven_app.dto.CropRequestDto;
import com.greenheaven.greenheaven_app.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Controller
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    private final NotificationService notificationService;

    // Last-Event-ID는 SSE 연결이 끊어진 경우, 클라이언트가 수신한 마지막 데이터의 ID를 의미한다. 항상 존재하는 것이 아니므로 false
    // SSE 연결이 끊겼을 때 재연결하는 과정에서 브라우저가 자동으로 마지막 이벤트 ID(Last-Event-ID)를 포함해서 서버에 요청을 보낸다
    // 한 번 이 API를 호출해두면, 브라우저가 자동으로 재연결을 반복해준다.
    @GetMapping(value = "/notification/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> connect(@RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        return ResponseEntity.ok(notificationService.connect(lastEventId));
    }
}

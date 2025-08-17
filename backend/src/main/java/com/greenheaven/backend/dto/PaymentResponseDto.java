package com.greenheaven.backend.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class PaymentResponseDto {
    private Integer amount; // 결제 금액

    private LocalDateTime createDate; // 결제 시간
    
    private String reservMenuName; // 결제한 메뉴명

    private UUID paymentId; // 결제 아이디

    private String userEmail; // 유저 이메일

    private String userName; // 유저 이름

    private String successUrl; // 성공 시 리다이렉트 할 URL

    private String failUrl; // 실패 시 리다이렉트 할 URL

    private String failReason; // 실패 이유

    private boolean cancelYN; // 취소 YN

    private String cancelReason; // 취소 이유

}

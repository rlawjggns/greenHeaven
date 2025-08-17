package com.greenheaven.backend.domain;

import lombok.Getter;

@Getter
public enum NotificationType {
    GENERAL("일반 알림"),
    REMINDER("리마인더(상기) 알림"),
    PROMOTION("프로모션(판촉) 알림"),
    SYSTEM("시스템 알림"),
    ALERT("경고 알림");

    private final String korName;

    NotificationType(String korName) {
        this.korName = korName;
    }
}

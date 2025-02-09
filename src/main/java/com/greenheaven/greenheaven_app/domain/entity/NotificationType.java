package com.greenheaven.greenheaven_app.domain.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

public enum NotificationType {
    GENERAL,        // 일반 알림
    REMINDER,       // 리마인더(상기) 알림
    PROMOTION,      // 프로모션(판촉) 알림
    SYSTEM          // 시스템 알림
}

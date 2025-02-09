package com.greenheaven.greenheaven_app.domain.entity;

import jakarta.persistence.Enumerated;

public enum UserRole {
    USER, // 일반 유저 
    ADMIN // 관리자
}

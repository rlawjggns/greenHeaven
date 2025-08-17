package com.greenheaven.backend.domain;

import lombok.Getter;

@Getter
public enum CropType {
    LETTUCE("상추"),         // 상추
    RADISH("무"),          // 무
    POTATO("감자"),         // 감자
    SWEETPOTATO("고구마"); // 고구마

    private final String korName;

    CropType(String korName) {
        this.korName = korName;
    }
}

package com.greenheaven.greenheaven_app.domain;

import lombok.Getter;

@Getter
public enum Sky {
    LUCIDITY("맑음"),
    CLOUDY("구름많음"),
    BLUR("흐림");

    private final String displayName;

    Sky(String displayName) {
        this.displayName = displayName;
    }
}

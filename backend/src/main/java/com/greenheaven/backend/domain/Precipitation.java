package com.greenheaven.backend.domain;

import lombok.Getter;

@Getter
public enum Precipitation {
    NONE("맑음"),
    RAIN("비"),
    RAINSNOW("비와눈"),
    SNOW("눈"),
    RAINDROP("빗방울"),
    RAINDROPBLAST("빗방울과눈날림"),
    BLAST("눈날림");

    private final String displayName;

    Precipitation(String displayName) {
        this.displayName = displayName;
    }

}

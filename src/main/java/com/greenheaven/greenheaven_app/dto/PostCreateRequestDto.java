package com.greenheaven.greenheaven_app.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class PostCreateRequestDto {
    private String title;
    private String content;
}

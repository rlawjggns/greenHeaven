package com.greenheaven.greenheaven_app.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class PostDetailResponseDto {
    private UUID id;
    private String title;
    private String content;
    private Integer views;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String memberName;
}

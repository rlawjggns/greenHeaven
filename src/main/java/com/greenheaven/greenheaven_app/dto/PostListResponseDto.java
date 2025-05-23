package com.greenheaven.greenheaven_app.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class PostListResponseDto {
    private UUID id;
    private String title;
    private LocalDateTime createDate;
    private Integer views;
    private String memberName;
}

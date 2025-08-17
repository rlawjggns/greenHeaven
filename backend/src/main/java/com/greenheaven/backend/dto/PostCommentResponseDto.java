package com.greenheaven.backend.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class PostCommentResponseDto {
    private UUID id;
    private String content;
    private LocalDateTime createdDate;
    private String memberName;
    private String memberEmail;
}

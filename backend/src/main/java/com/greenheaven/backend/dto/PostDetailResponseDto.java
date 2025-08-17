package com.greenheaven.backend.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
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
    private String memberEmail;
    private List<PostCommentResponseDto> postComments;
}

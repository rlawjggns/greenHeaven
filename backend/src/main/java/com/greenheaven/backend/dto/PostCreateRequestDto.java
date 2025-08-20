package com.greenheaven.backend.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostCreateRequestDto {
    private String id;
    private String title;
    private String content;
}

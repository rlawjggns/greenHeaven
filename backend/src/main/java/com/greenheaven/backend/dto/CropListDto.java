package com.greenheaven.backend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class CropListDto {
    private UUID id;       // 추가: 작물의 고유 ID
    private String type;
    private String name;
}

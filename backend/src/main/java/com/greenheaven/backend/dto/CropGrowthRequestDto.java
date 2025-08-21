package com.greenheaven.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CropGrowthRequestDto {
    private String reason; // 사유
    private Integer adjustDays; // 조정일 (+, -)
}

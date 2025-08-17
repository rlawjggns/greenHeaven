package com.greenheaven.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CropGrowthDto {
    private UUID id;
    private Integer adjustDays;
}

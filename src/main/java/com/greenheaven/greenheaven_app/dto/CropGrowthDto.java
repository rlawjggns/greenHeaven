package com.greenheaven.greenheaven_app.dto;

import com.greenheaven.greenheaven_app.domain.CropType;
import lombok.Builder;
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

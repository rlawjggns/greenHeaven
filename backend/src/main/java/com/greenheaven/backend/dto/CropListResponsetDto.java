package com.greenheaven.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class CropListResponsetDto {
    @NotNull
    private UUID id;

    @NotBlank
    private String name;

    @NotBlank
    private String typeName;

    @NotNull
    private LocalDate plantDate;

    @NotNull
    private LocalDate harvestDate;

    @NotNull
    private Long remainDays;

    @NotNull
    private Double quantity;
}

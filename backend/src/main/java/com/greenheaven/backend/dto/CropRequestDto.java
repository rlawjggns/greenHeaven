package com.greenheaven.backend.dto;

import com.greenheaven.backend.domain.CropType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class CropRequestDto {
    @NotBlank
    private String name;

    @NotBlank
    private CropType type;

    @NotNull
    private LocalDate plantDate;

    @NotNull
    private Double quantity;
}

package com.greenheaven.greenheaven_app.dto;

import com.greenheaven.greenheaven_app.domain.CropType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
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

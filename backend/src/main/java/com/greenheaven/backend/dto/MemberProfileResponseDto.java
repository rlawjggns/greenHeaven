package com.greenheaven.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberProfileResponseDto {
    private String oldPassword;

    private String name;

    private String address;
}

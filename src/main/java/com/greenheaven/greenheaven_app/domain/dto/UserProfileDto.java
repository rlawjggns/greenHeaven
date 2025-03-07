package com.greenheaven.greenheaven_app.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
public class UserProfileDto {
    @NotBlank(message = "기본 비밀번호를 입력하세요.")
    private String oldPassword;

    private String newPassword;

    private String confirmNewPassword;

    @NotBlank(message = "이름을 입력해주세요.")
    private String name;
}

package com.greenheaven.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberProfileDto {
    @NotBlank(message = "기본 비밀번호를 입력하세요.")
    private String oldPassword;

    private String newPassword;

    private String confirmNewPassword;

    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @NotBlank(message = "주소를 입력해주세요.")
    private String address;
}

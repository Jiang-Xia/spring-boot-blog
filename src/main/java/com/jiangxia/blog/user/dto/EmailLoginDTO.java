package com.jiangxia.blog.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "邮箱登录DTO")
@Getter
@Setter
public class EmailLoginDTO {

    @Schema(description = "邮箱地址", example = "jiangxia2048@163.com")
    @Email(message = "请输入正确的邮箱地址")
    @NotBlank(message = "请输入邮箱")
    private String email;

    @Schema(description = "密码", example = "123456")
    @NotBlank(message = "请输入密码")
    private String password;

    @Schema(description = "邮箱验证码", example = "123456")
    @NotBlank(message = "请输入邮箱验证码")
    private String verificationCode;

    @Schema(description = "是否为管理端", example = "true")
    private Boolean admin;
}

package com.jiangxia.blog.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "邮箱注册DTO")
@Getter
@Setter
public class EmailRegisterDTO {

    @Schema(description = "邮箱地址", example = "jiangxia2048@163.com")
    @Email(message = "请输入正确的邮箱地址")
    @NotBlank(message = "请输入邮箱")
    private String email;

    @Schema(description = "昵称", example = "张三")
    @NotBlank(message = "请输入用户昵称")
    @Size(max = 50, message = "昵称不能超过50个字符")
    private String nickname;

    @Schema(description = "密码", example = "123456")
    @NotBlank(message = "请输入密码")
    @Size(min = 6, max = 18, message = "密码长度为6-18个字符")
    private String password;

    @Schema(description = "确认密码", example = "123456")
    @NotBlank(message = "请再次输入密码")
    private String passwordRepeat;

    @Schema(description = "邮箱验证码", example = "123456")
    @NotBlank(message = "请输入邮箱验证码")
    private String verificationCode;

    @Schema(description = "头像", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Schema(description = "角色", example = "user")
    private String role;
}

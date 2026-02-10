package com.jiangxia.blog.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "用户登录请求DTO")
@Getter
@Setter
public class LoginRequest {

    @Schema(description = "用户名或邮箱", example = "jiangxia")
    private String username;

    @Schema(description = "手机号", example = "18888888888")
    private String mobile;

    @Schema(description = "密码", example = "jx123456!@#")
    @NotBlank(message = "密码不能为空")
    private String password;

    @Schema(description = "验证码", example = "123456")
    @NotBlank(message = "请输入验证码")
    private String authCode;
}

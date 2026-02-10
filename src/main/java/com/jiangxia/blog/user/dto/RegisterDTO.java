package com.jiangxia.blog.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "用户注册DTO")
@Getter
@Setter
public class RegisterDTO {

    @Schema(description = "昵称", example = "江夏")
    @NotBlank(message = "请输入用户昵称")
    @Size(max = 50, message = "昵称不能超过50个字符")
    private String nickname;

    @Schema(description = "手机号/账号", example = "13049153466")
    @NotBlank(message = "请输入账号")
    @Size(min = 6, max = 11, message = "账号长度为6-11个字符")
    @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "账号只允许字母、数字、中划线")
    private String mobile;

    @Schema(description = "密码", example = "123456")
    @NotBlank(message = "请输入密码")
    @Size(min = 6, max = 18, message = "密码长度为6-18个字符")
    private String password;

    @Schema(description = "确认密码", example = "123456")
    @NotBlank(message = "请再次输入密码")
    private String passwordRepeat;

    @Schema(description = "验证码", example = "123456")
    @NotBlank(message = "请输入验证码")
    private String authCode;

    @Schema(description = "头像", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Schema(description = "主页", example = "https://example.com")
    private String homepage;

    @Schema(description = "用户名", example = "jiangxia")
    private String username;

}

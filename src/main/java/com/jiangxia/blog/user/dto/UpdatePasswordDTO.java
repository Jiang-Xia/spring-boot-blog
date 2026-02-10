package com.jiangxia.blog.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "修改密码DTO")
@Getter
@Setter
public class UpdatePasswordDTO {

    @Schema(description = "用户ID", example = "1")
    @NotNull(message = "用户ID不能为空")
    private Long id;

    @Schema(description = "旧密码", example = "123456")
    @NotBlank(message = "请输入旧密码")
    private String passwordOld;

    @Schema(description = "新密码", example = "654321")
    @NotBlank(message = "请输入新密码")
    @Size(min = 6, max = 18, message = "密码长度为6-18个字符")
    private String password;

    @Schema(description = "确认密码", example = "654321")
    @NotBlank(message = "请再次输入密码")
    private String passwordRepeat;
}

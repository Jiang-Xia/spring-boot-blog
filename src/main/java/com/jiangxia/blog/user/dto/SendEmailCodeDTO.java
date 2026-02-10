package com.jiangxia.blog.user.dto;

import com.jiangxia.blog.user.entity.EmailCodeType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "发送邮箱验证码DTO")
@Getter
@Setter
public class SendEmailCodeDTO {

    @Schema(description = "邮箱地址", example = "jiangxia2048@163.com")
    @Email(message = "请输入正确的邮箱地址")
    @NotBlank(message = "请输入邮箱")
    private String email;

    @Schema(description = "验证码类型", example = "REGISTER")
    @NotNull(message = "请指定验证码类型")
    private EmailCodeType type;
}

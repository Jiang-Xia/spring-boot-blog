package com.jiangxia.blog.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "更新用户信息DTO")
@Getter
@Setter
public class UpdateUserDTO {

    @Schema(description = "用户ID", example = "1")
    @NotNull(message = "用户ID不能为空")
    private Long id;

    @Schema(description = "昵称", example = "江夏")
    @Size(max = 50, message = "昵称不能超过50个字符")
    private String nickname;

    @Schema(description = "简介", example = "这是一个简介")
    @Size(max = 200, message = "简介不能超过200个字符")
    private String intro;

    @Schema(description = "头像", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Schema(description = "个人主页", example = "https://jiangxia.com")
    private String homepage;
}

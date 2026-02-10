package com.jiangxia.blog.user.dto;

import com.jiangxia.blog.user.entity.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "更新用户状态DTO")
@Getter
@Setter
public class UpdateUserStatusDTO {

    @Schema(description = "用户ID", example = "1")
    @NotNull(message = "用户ID不能为空")
    private Long id;

    @Schema(description = "用户状态", example = "ACTIVE")
    @NotNull(message = "用户状态不能为空")
    private UserStatus status;
}

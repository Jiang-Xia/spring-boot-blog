package com.jiangxia.blog.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Schema(description = "管理员更新用户DTO")
@Getter
@Setter
public class AdminUpdateUserDTO {

    @Schema(description = "昵称", example = "张三")
    @NotBlank(message = "请输入用户昵称")
    @Size(min = 2, max = 50, message = "昵称长度为2-50个字符")
    private String nickname;

    @Schema(description = "角色ID列表", example = "[1, 2]")
    private List<Long> roleIds;

    @Schema(description = "部门ID", example = "1")
    private Integer deptId;

    @Schema(description = "简介", example = "这是一个用户简介")
    @Size(max = 200, message = "简介不能超过200个字符")
    private String intro;

    @Schema(description = "头像", example = "https://example.com/avatar.jpg")
    private String avatar;
}

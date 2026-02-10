package com.jiangxia.blog.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Schema(description = "管理员创建用户DTO")
@Getter
@Setter
public class AdminCreateUserDTO {

    @Schema(description = "昵称", example = "张三")
    @NotBlank(message = "请输入用户昵称")
    @Size(min = 2, max = 50, message = "昵称长度为2-50个字符")
    private String nickname;

    @Schema(description = "用户名", example = "zhangsan")
    @NotBlank(message = "请输入用户名")
    @Size(min = 6, max = 11, message = "用户名长度为6-11个字符")
    @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "用户名只允许字母、数字、中划线")
    private String username;

    @Schema(description = "密码", example = "123456")
    @NotBlank(message = "请输入密码")
    @Size(min = 6, max = 18, message = "密码长度为6-18个字符")
    private String password;

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

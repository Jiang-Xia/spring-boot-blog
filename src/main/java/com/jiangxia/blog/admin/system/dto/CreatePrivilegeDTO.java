package com.jiangxia.blog.admin.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "创建权限DTO")
public class CreatePrivilegeDTO {

    @Schema(description = "权限名称", example = "用户管理")
    @NotBlank(message = "请输入权限名称")
    @Size(min = 2, max = 50, message = "权限名称长度需要在2-50个字符之间")
    private String privilegeName;

    @Schema(description = "权限识别码", example = "user:manage")
    @NotBlank(message = "请输入权限识别码")
    @Size(min = 2, max = 100, message = "权限识别码长度需要在2-100个字符之间")
    private String privilegeCode;

    @Schema(description = "所属页面(菜单id)", example = "user/index")
    @Size(max = 100, message = "所属页面不能超过100个字符")
    private String privilegePage;

    @Schema(description = "是否可见", example = "true")
    private Boolean isVisible;

    @Schema(description = "路径模式，如 /api/users/:id", example = "/api/users/:id")
    @Size(max = 500, message = "路径模式不能超过500个字符")
    private String pathPattern;

    @Schema(description = "HTTP方法，*表示全部", example = "GET")
    @Size(max = 10, message = "HTTP方法不能超过10个字符")
    private String httpMethod;

    @Schema(description = "是否公开接口", example = "false")
    private Boolean isPublic;

    @Schema(description = "是否需要检查资源所有权", example = "false")
    private Boolean requireOwnership;

    @Schema(description = "描述", example = "这是一个权限描述")
    @Size(max = 500, message = "描述不能超过500个字符")
    private String description;
}

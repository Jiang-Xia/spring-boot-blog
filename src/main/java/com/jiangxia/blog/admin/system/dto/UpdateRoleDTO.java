package com.jiangxia.blog.admin.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "更新角色DTO")
public class UpdateRoleDTO {

    @Schema(description = "角色名", example = "admin")
    @Size(min = 2, max = 50, message = "角色名长度需要在2-50个字符之间")
    private String roleName;

    @Schema(description = "角色描述", example = "管理员角色")
    @Size(max = 200, message = "角色描述不能超过200个字符")
    private String roleDesc;

    @Schema(description = "权限ID数组", example = "[1, 2, 3]")
    private List<Long> privileges;

    @Schema(description = "菜单ID数组", example = "[\"menu1\", \"menu2\"]")
    private List<String> menus;
}

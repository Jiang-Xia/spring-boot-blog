package com.jiangxia.blog.admin.system.controller;

import com.jiangxia.blog.admin.system.dto.CreateRoleDTO;
import com.jiangxia.blog.admin.system.dto.UpdateRoleDTO;
import com.jiangxia.blog.admin.system.entity.Role;
import com.jiangxia.blog.admin.system.service.RoleService;
import com.jiangxia.blog.admin.system.vo.MenuPrivilegeTreeNode;
import com.jiangxia.blog.admin.system.vo.RoleListVO;
import com.jiangxia.blog.common.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "角色模块", description = "角色管理相关接口")
@RestController
@RequestMapping("/role")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @Operation(summary = "获取菜单权限树", description = "获取菜单权限树形数据，用于角色分配")
    @GetMapping("/menu-privilege-tree")
    public List<MenuPrivilegeTreeNode> getMenuPrivilegeTree() {
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return roleService.getMenuPrivilegeTree();
    }

    @Operation(summary = "创建角色", description = "创建新角色并关联权限和菜单")
    @PostMapping
    public Role create(@Valid @RequestBody CreateRoleDTO dto) {
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return roleService.create(dto);
    }

    @Operation(summary = "查询角色列表", description = "分页查询角色列表，支持按角色名筛选")
    @GetMapping
    public RoleListVO read(@RequestParam Map<String, Object> queryParams) {
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return roleService.read(queryParams);
    }

    @Operation(summary = "查询角色详情", description = "根据ID查询角色详细信息")
    @GetMapping("/{id}")
    public Role queryInfo(@PathVariable Long id) {
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return roleService.queryInfo(id);
    }

    @Operation(summary = "更新角色", description = "更新角色信息及关联的权限和菜单")
    @PatchMapping("/{id}")
    public Role update(@PathVariable Long id, @Valid @RequestBody UpdateRoleDTO dto) {
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return roleService.update(id, dto);
    }

    @Operation(summary = "删除角色", description = "根据ID删除角色")
    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Long id) {
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return roleService.delete(id);
    }
}

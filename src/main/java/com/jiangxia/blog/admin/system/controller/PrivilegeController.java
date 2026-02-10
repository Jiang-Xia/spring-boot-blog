package com.jiangxia.blog.admin.system.controller;

import com.jiangxia.blog.admin.system.dto.CreatePrivilegeDTO;
import com.jiangxia.blog.admin.system.dto.UpdatePrivilegeDTO;
import com.jiangxia.blog.admin.system.entity.Privilege;
import com.jiangxia.blog.admin.system.service.PrivilegeService;
import com.jiangxia.blog.admin.system.vo.PrivilegeListVO;
import com.jiangxia.blog.common.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "权限模块", description = "权限管理相关接口")
@RestController
@RequestMapping("/privilege")
public class PrivilegeController {

    private final PrivilegeService privilegeService;

    public PrivilegeController(PrivilegeService privilegeService) {
        this.privilegeService = privilegeService;
    }

    @Operation(summary = "创建权限", description = "创建新权限")
    @PostMapping
    public Privilege create(@Valid @RequestBody CreatePrivilegeDTO dto) {
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return privilegeService.create(dto);
    }

    @Operation(summary = "查询权限列表", description = "分页查询权限列表，支持按权限名筛选")
    @GetMapping
    public PrivilegeListVO read(@RequestParam Map<String, Object> queryParams) {
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return privilegeService.read(queryParams);
    }

    @Operation(summary = "查询权限详情", description = "根据ID查询权限详细信息")
    @GetMapping("/{id}")
    public Privilege queryInfo(@PathVariable Long id) {
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return privilegeService.queryInfo(id);
    }

    @Operation(summary = "更新权限", description = "更新权限信息")
    @PatchMapping("/{id}")
    public Privilege update(@PathVariable Long id, @Valid @RequestBody UpdatePrivilegeDTO dto) {
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return privilegeService.update(id, dto);
    }

    @Operation(summary = "删除权限", description = "根据ID删除权限")
    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Long id) {
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return privilegeService.delete(id);
    }
}

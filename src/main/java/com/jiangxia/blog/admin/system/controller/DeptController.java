package com.jiangxia.blog.admin.system.controller;

import com.jiangxia.blog.admin.system.dto.CreateDeptDTO;
import com.jiangxia.blog.admin.system.dto.UpdateDeptDTO;
import com.jiangxia.blog.admin.system.entity.Dept;
import com.jiangxia.blog.admin.system.service.DeptService;
import com.jiangxia.blog.admin.system.vo.DeptListVO;
import com.jiangxia.blog.common.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "部门模块", description = "部门管理相关接口")
@RestController
@RequestMapping("/dept")
public class DeptController {

    private final DeptService deptService;

    public DeptController(DeptService deptService) {
        this.deptService = deptService;
    }

    @Operation(summary = "创建部门", description = "创建新部门")
    @PostMapping
    public Dept create(@Valid @RequestBody CreateDeptDTO dto) {
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return deptService.create(dto);
    }

    @Operation(summary = "查询部门列表", description = "分页查询部门列表，支持按部门名和父级ID筛选")
    @GetMapping
    public DeptListVO read(@RequestParam Map<String, Object> queryParams) {
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return deptService.read(queryParams);
    }

    @Operation(summary = "查询部门树", description = "查询部门树形结构，可指定父级ID")
    @GetMapping("/tree")
    public List<Dept> queryTree(@RequestParam(required = false) Long id) {
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return deptService.queryTree(id);
    }

    @Operation(summary = "查询部门详情", description = "根据ID查询部门详细信息")
    @GetMapping("/{id}")
    public Dept queryInfo(@PathVariable Long id) {
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return deptService.queryInfo(id);
    }

    @Operation(summary = "更新部门", description = "更新部门信息")
    @PatchMapping("/{id}")
    public Dept update(@PathVariable Long id, @Valid @RequestBody UpdateDeptDTO dto) {
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return deptService.update(id, dto);
    }

    @Operation(summary = "删除部门", description = "根据ID删除部门，若存在子部门则不允许删除")
    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Long id) {
        // 现在可以不手动包装，由GlobalResponseAdvice自动处理
        return deptService.delete(id);
    }
}

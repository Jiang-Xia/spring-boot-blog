package com.jiangxia.blog.admin.system.vo;

import com.jiangxia.blog.admin.system.entity.Privilege;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Schema(description = "权限简单信息VO")
@Getter
@Setter
public class PrivilegeSimpleVO {

    @Schema(description = "权限ID")
    private Long id;

    @Schema(description = "权限名称")
    private String privilegeName;

    @Schema(description = "权限编码")
    private String privilegeCode;

    @Schema(description = "权限页面")
    private String privilegePage;

    @Schema(description = "是否可见")
    private Boolean isVisible;

    @Schema(description = "路径模式")
    private String pathPattern;

    @Schema(description = "HTTP方法")
    private String httpMethod;

    @Schema(description = "是否公开")
    private Boolean isPublic;

    @Schema(description = "需要所有权验证")
    private Boolean requireOwnership;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    // 静态工厂方法
    public static PrivilegeSimpleVO fromEntity(Privilege privilege) {
        PrivilegeSimpleVO vo = new PrivilegeSimpleVO();
        vo.setId(privilege.getId());
        vo.setPrivilegeName(privilege.getPrivilegeName());
        vo.setPrivilegeCode(privilege.getPrivilegeCode());
        vo.setPrivilegePage(privilege.getPrivilegePage());
        vo.setIsVisible(privilege.getIsVisible());
        vo.setPathPattern(privilege.getPathPattern());
        vo.setHttpMethod(privilege.getHttpMethod());
        vo.setIsPublic(privilege.getIsPublic());
        vo.setRequireOwnership(privilege.getRequireOwnership());
        vo.setDescription(privilege.getDescription());
        vo.setCreateTime(privilege.getCreateTime());
        vo.setUpdateTime(privilege.getUpdateTime());
        return vo;
    }
}
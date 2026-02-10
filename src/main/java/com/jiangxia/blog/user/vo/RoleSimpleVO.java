package com.jiangxia.blog.user.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.jiangxia.blog.admin.system.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Schema(description = "简化角色信息VO")
@Getter
@Setter
public class RoleSimpleVO {
    
    @Schema(description = "角色ID")
    private Long id;
    
    @Schema(description = "角色名称")
    private String roleName;
    
    @Schema(description = "角色描述")
    private String roleDesc;
    
    @Schema(description = "创建时间")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    
    // Static factory method
    public static RoleSimpleVO fromEntity(Role role) {
        if (role == null) {
            return null;
        }
        RoleSimpleVO vo = new RoleSimpleVO();
        vo.setId(role.getId());
        vo.setRoleName(role.getRoleName());
        vo.setRoleDesc(role.getRoleDesc());
        vo.setCreateTime(role.getCreateTime());
        vo.setUpdateTime(role.getUpdateTime());
        return vo;
    }
}
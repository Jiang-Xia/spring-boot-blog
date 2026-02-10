package com.jiangxia.blog.user.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.jiangxia.blog.admin.system.entity.Dept;
import com.jiangxia.blog.admin.system.entity.Privilege;
import com.jiangxia.blog.admin.system.entity.Role;
import com.jiangxia.blog.admin.system.vo.PrivilegeSimpleVO;
import com.jiangxia.blog.user.entity.User;
import com.jiangxia.blog.user.entity.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Schema(description = "用户信息VO")
@Getter
@Setter
public class UserInfoVO {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "简介")
    private String intro;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "个人主页")
    private String homepage;

    @Schema(description = "用户状态")
    private UserStatus status;

    @Schema(description = "部门ID")
    private Integer deptId;

    @Schema(description = "部门信息")
    private Dept dept;

    @Schema(description = "角色列表")
    private Set<RoleSimpleVO> roles;

    @Schema(description = "权限列表")
    private Set<PrivilegeSimpleVO> privileges;

    @Schema(description = "创建时间")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    // Static factory method
    public static UserInfoVO fromEntity(User user) {
        UserInfoVO vo = new UserInfoVO();
        vo.setId(user.getId());
        vo.setNickname(user.getNickname());
        vo.setUsername(user.getUsername());
        vo.setMobile(user.getMobile());
        vo.setEmail(user.getEmail());
        vo.setIntro(user.getIntro());
        vo.setAvatar(user.getAvatar());
        vo.setHomepage(user.getHomepage());
        vo.setStatus(user.getStatus());
        vo.setDeptId(user.getDeptId());
        // 转换角色集合为简化版
        if (user.getRoles() != null) {
            vo.setRoles(user.getRoles().stream()
                    .map(RoleSimpleVO::fromEntity)
                    .collect(Collectors.toSet()));
        }
        
        // 提取所有角色的权限
        if (user.getRoles() != null) {
            Set<PrivilegeSimpleVO> allPrivileges = user.getRoles().stream()
                    .flatMap(role -> role.getPrivileges().stream())
                    .map(PrivilegeSimpleVO::fromEntity)
                    .collect(Collectors.toSet());
            vo.setPrivileges(allPrivileges);
        }
        
        vo.setCreateTime(user.getCreateTime());
        vo.setUpdateTime(user.getUpdateTime());
        return vo;
    }
}

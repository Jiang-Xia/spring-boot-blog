package com.jiangxia.blog.security.util;

import com.jiangxia.blog.admin.system.entity.Privilege;
import com.jiangxia.blog.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限检查工具类
 */
@Component
public class PermissionCheckUtil {

    /**
     * 检查用户是否有特定权限
     *
     * @param user       用户
     * @param permissionCode 权限代码
     * @return 是否有权限
     */
    public boolean hasPermission(User user, String permissionCode) {
        if (user == null || user.getRoles() == null) {
            return false;
        }

        // 检查用户的角色是否包含指定权限
        return user.getRoles().stream()
                .flatMap(role -> role.getPrivileges().stream())
                .anyMatch(privilege -> privilege.getPrivilegeCode().equals(permissionCode));
    }

    /**
     * 检查用户是否有特定权限列表中的任一权限
     *
     * @param user       用户
     * @param permissions 权限代码列表
     * @return 是否有权限
     */
    public boolean hasAnyPermission(User user, List<String> permissions) {
        if (user == null || user.getRoles() == null || permissions == null || permissions.isEmpty()) {
            return false;
        }

        Set<String> userPermissions = user.getRoles().stream()
                .flatMap(role -> role.getPrivileges().stream())
                .map(Privilege::getPrivilegeCode)
                .collect(Collectors.toSet());

        return userPermissions.stream()
                .anyMatch(permissions::contains);
    }

    /**
     * 检查用户是否具有所有指定权限
     *
     * @param user       用户
     * @param permissions 权限代码列表
     * @return 是否有所有权限
     */
    public boolean hasAllPermissions(User user, List<String> permissions) {
        if (user == null || user.getRoles() == null || permissions == null || permissions.isEmpty()) {
            return false;
        }

        Set<String> userPermissions = user.getRoles().stream()
                .flatMap(role -> role.getPrivileges().stream())
                .map(Privilege::getPrivilegeCode)
                .collect(Collectors.toSet());

        return userPermissions.containsAll(permissions);
    }

    /**
     * 检查用户是否为超级管理员
     *
     * @param user 用户
     * @return 是否为超级管理员
     */
    public boolean isSuperAdmin(User user) {
        if (user == null || user.getRoles() == null) {
            return false;
        }

        // 假设超级管理员角色ID为1，可以根据实际需求调整
        return user.getRoles().stream()
                .anyMatch(role -> "超级管理员".equals(role.getRoleName()));
    }
}
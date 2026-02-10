package com.jiangxia.blog.security.aop;

import com.jiangxia.blog.common.exception.BizException;
import com.jiangxia.blog.security.annotation.RequiresPermissions;
import com.jiangxia.blog.security.util.PermissionCheckUtil;
import com.jiangxia.blog.user.entity.User;
import com.jiangxia.blog.user.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 权限检查切面
 */
@Aspect
@Component
public class PermissionAspect {

    @Autowired
    private PermissionCheckUtil permissionCheckUtil;

    @Autowired
    private UserService userService;

    @Around("@annotation(requiresPermissions)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequiresPermissions requiresPermissions) throws Throwable {
        // 获取当前认证用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new BizException("用户未登录或认证失败");
        }

        // 获取当前用户名
        String username = authentication.getName();
        
        // 从数据库获取完整用户信息（包含角色和权限）
        User user = userService.findByUsernameOrEmail(username);

        // 检查权限
        String[] permissions = requiresPermissions.value();
        RequiresPermissions.Logical logical = requiresPermissions.logical();

        boolean hasPermission = false;
        if (permissions.length == 0) {
            // 如果没有指定权限，则认为有权限
            hasPermission = true;
        } else if (logical == RequiresPermissions.Logical.AND) {
            // 必须具有所有权限
            hasPermission = permissionCheckUtil.hasAllPermissions(user, Arrays.asList(permissions));
        } else if (logical == RequiresPermissions.Logical.OR) {
            // 具有任一权限即可
            hasPermission = permissionCheckUtil.hasAnyPermission(user, Arrays.asList(permissions));
        }

        if (!hasPermission) {
            throw new BizException("权限不足，无法访问此资源");
        }

        // 执行原方法
        return joinPoint.proceed();
    }
}
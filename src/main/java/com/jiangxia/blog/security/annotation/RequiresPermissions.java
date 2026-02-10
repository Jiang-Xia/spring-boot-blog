package com.jiangxia.blog.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 需要权限的注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermissions {
    /**
     * 需要的权限
     */
    String[] value() default {};

    /**
     * 权限之间的逻辑关系
     */
    Logical logical() default Logical.AND;
    
    enum Logical {
        /**
         * 必须具有所有权限
         */
        AND,
        
        /**
         * 具有任一权限即可
         */
        OR
    }
}
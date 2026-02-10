package com.jiangxia.blog.user.entity;

import lombok.Getter;

@Getter
public enum UserStatus {
    LOCKED,
    ACTIVE;

    public static UserStatus fromString(String value) {
        if (value == null) return null;
        try {
            return UserStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ACTIVE; // 默认返回 ACTIVE 或者抛出更友好的异常
        }
    }
}

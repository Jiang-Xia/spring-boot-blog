package com.jiangxia.blog.user.entity;

import lombok.Getter;

@Getter
public enum EmailCodeType {
    REGISTER("register", "注册"),
    LOGIN("login", "登录"),
    RESET("reset", "重置密码");

    private final String code;
    private final String description;

    EmailCodeType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static EmailCodeType fromCode(String code) {
        for (EmailCodeType type : EmailCodeType.values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的邮箱验证码类型: " + code);
    }
}

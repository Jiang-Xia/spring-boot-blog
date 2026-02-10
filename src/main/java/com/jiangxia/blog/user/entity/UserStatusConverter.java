package com.jiangxia.blog.user.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserStatusConverter implements AttributeConverter<UserStatus, String> {

    @Override
    public String convertToDatabaseColumn(UserStatus attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name();
    }

    @Override
    public UserStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return UserStatus.valueOf(dbData.toUpperCase());
        } catch (IllegalArgumentException e) {
            // 如果数据库中的值不匹配任何枚举常量，可以返回默认值或抛出异常
            return UserStatus.ACTIVE;
        }
    }
}

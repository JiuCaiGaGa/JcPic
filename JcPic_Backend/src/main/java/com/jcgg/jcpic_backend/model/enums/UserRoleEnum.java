package com.jcgg.jcpic_backend.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

@Getter
public enum UserRoleEnum {
    // 普通用户
    USER("普通用户", "user"),
    // vip
    VIP("VIP", "VIP"),
    // 管理员
    ADMIN("管理员", "admin");

    private final String role;
    private final String value;

    UserRoleEnum(String role, String value) {
        this.role = role;
        this.value = value;
    }

    /**
     * 通过 value 查询对应的枚举值
     *
     * @param value 值
     * @return value 对应的 枚举值
     */
    public static UserRoleEnum getEnumByValue(String value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (UserRoleEnum userRoleEnum : UserRoleEnum.values()) {
            if (userRoleEnum.value.equals(value)) {
                return userRoleEnum;
            }
        }
        return null;
    }
}

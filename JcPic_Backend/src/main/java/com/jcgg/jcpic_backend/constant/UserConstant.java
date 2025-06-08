package com.jcgg.jcpic_backend.constant;

/**
 * 用户常量
 */
public interface UserConstant {
    // 用户登录状态
    String USER_LOGIN_STATE = "user_login_status";

    // region 用户组权限
    // 1. 默认普通用户
    String DEFAULT_ROLE = "user";
    // 2. vip
    String VIP_ROLE = "vip";
    // 3. 管理员
    String ADMIN_ROLE = "admin";
    // endregion

}

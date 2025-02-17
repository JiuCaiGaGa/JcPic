package com.jcgg.jcpic_backend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 处理 用户登录请求
 */

@Data
public class UserLoginRequest implements Serializable {

    private String userAccount;
    private String userPassword;

    private static final long serialVersionUID = 682617267802049357L;
}

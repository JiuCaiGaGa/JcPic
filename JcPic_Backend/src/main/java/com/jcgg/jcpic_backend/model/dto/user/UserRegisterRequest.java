package com.jcgg.jcpic_backend.model.dto.user;
import lombok.Data;
import java.io.Serializable;

/**
 * 处理 用户注册请求
 */

@Data
public class UserRegisterRequest implements Serializable {

    private String userAccount;
    private String userPassword;
    private String checkPassword;

    private static final long serialVersionUID = -5600258824402604655L;
}

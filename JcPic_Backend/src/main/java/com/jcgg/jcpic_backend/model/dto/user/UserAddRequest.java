package com.jcgg.jcpic_backend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户创建请求
 *
 */
@Data
public class UserAddRequest implements Serializable {


    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;


    /**
     * 用户角色: user, vip, admin
     */
    private String userRole;

    private static final long serialVersionUID = 2804893837578681483L;

}
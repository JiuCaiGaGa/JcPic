package com.jcgg.jcpic_backend.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * 用户视图(脱敏)
 */
@Data
public class UserVO {
    /**
     * id
     */
    private Long id;

    /**
     * 账号
     */
    private String userAccount;


    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：user/vip/admin
     */
    private String userRole;


    /**
     * 创建时间
     */
    private Date createTime;


}
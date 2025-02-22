package com.jcgg.jcpic_backend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jcgg.jcpic_backend.model.dto.user.UserQueryRequest;
import com.jcgg.jcpic_backend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jcgg.jcpic_backend.model.vo.LoginUserVO;
import com.jcgg.jcpic_backend.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author GaG
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-02-17 17:00:32
*/
public interface UserService extends IService<User> {

    /**
     * 是否为管理员
     *
     * @param user
     * @return
     */
    boolean isAdmin(User user);


    /**
     *
     * @param userAccount 登陆用账户名
     * @param userPassword 密码
     * @param checkPassword 确认密码
     * @return 注册ID
     */
    Long userRegister(String userAccount,String userPassword,String checkPassword);

    /**
     * 获取加密后的密码
     * @param userPassword 加密前的密码
     * @return 加密后的密码
     */
    String getEncryptPassword(String userPassword);

    /**
     * 登录
     * @param userAccount 登录账户名
     * @param userPassword 密码
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);




    /**
     * 获取当前登录用户的信息
     * @param request 用户获取Session ID
     * @return 数据库获取到的User对象
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 用户退出登录
     * @param request 获取Session
     * @return
     */

    boolean userLogout(HttpServletRequest request);



    /**
     * 将 user 对象转换为 loginUserVO 对象
     * @param user 数据库获取的对象
     * @return 脱敏后信息
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 将 user 对象转换为 UserVO 对象
     * @param user 数据库获取的对象
     * @return 脱敏后信息
     */
    UserVO getUserVO(User user);

    /**
     * 将 user 对象转换为 UserVO 对象列表
     * @param userList 数据库获取的对象列表
     * @return 脱敏后信息列表
     */
    List<UserVO> getUserVOList(List<User> userList);

    /**
     * 获取查询条件
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);


}

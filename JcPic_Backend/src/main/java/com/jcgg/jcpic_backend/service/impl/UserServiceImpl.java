package com.jcgg.jcpic_backend.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jcgg.jcpic_backend.constant.UserConstant;
import com.jcgg.jcpic_backend.exception.BussinessException;
import com.jcgg.jcpic_backend.exception.ErrorCode;
import com.jcgg.jcpic_backend.exception.ThrowUtils;
import com.jcgg.jcpic_backend.model.dto.user.UserQueryRequest;
import com.jcgg.jcpic_backend.model.entity.User;
import com.jcgg.jcpic_backend.model.enums.UserRoleEnum;
import com.jcgg.jcpic_backend.model.vo.LoginUserVO;
import com.jcgg.jcpic_backend.model.vo.UserVO;
import com.jcgg.jcpic_backend.service.UserService;
import com.jcgg.jcpic_backend.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author GaG
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2025-02-17 17:00:32
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Override
    public Long userRegister(String userAccount, String userPassword, String checkPassword) {

        // 校验参数
        ThrowUtils.throwIf(StrUtil.hasBlank(
                        userAccount, userPassword, checkPassword),
                ErrorCode.PARAMS_ERROR, "参数不能为空"
        );
/*
        等效代码,其他省略
        if(StrUtil.hasBlank(userAccount,userPassword,checkPassword)){
            throw new BussinessException(ErrorCode.PARAMS_ERROR,"参数不能为空");
        }
 */
        ThrowUtils.throwIf(
                userAccount.length() < 3,
                ErrorCode.PARAMS_ERROR, "账户名太短,请重新输入"
        );
        ThrowUtils.throwIf(
                userPassword.length() < 8 || checkPassword.length() < 8,
                ErrorCode.PARAMS_ERROR, "密码过短,请重新输入"
        );
        ThrowUtils.throwIf(
                !userPassword.equals(checkPassword),
                ErrorCode.PARAMS_ERROR, "密码输入不一致"
        );

        // 是否和数据库中重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        long accountNums = this.baseMapper.selectCount(queryWrapper);
        ThrowUtils.throwIf(
                accountNums > 0,
                ErrorCode.PARAMS_ERROR,"该账户名不可用");
        // 密码加密
        String encryptPwd = getEncryptPassword(userPassword);
        // 插入数据到数据库
        User registerUser = new User();
        registerUser.setUserAccount(userAccount);
        registerUser.setUserPassword(encryptPwd);
        registerUser.setUserName("默认名称");
        registerUser.setUserRole(UserRoleEnum.USER.getValue());

        boolean saveResult = this.save(registerUser);
        ThrowUtils.throwIf(
                !saveResult,
                ErrorCode.SYSTEM_ERROR,"注册失败,请联系管理员"
        );

        return registerUser.getId();
    }

    /**
     *
     * @param userAccount 登录账户名
     * @param userPassword 密码
     * @param request 获取 Session
     * @return 脱敏后的用户信息
     */
    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 校验
        ThrowUtils.throwIf(StrUtil.hasBlank(
                        userAccount, userPassword),
                ErrorCode.PARAMS_ERROR, "参数不能为空"
        );
        ThrowUtils.throwIf(
                userAccount.length() < 3,
                ErrorCode.PARAMS_ERROR, "账户名错误,请重新输入"
        );
        ThrowUtils.throwIf(
                userPassword.length() < 8 ,
                ErrorCode.PARAMS_ERROR, "密码错误,请重新输入"
        );
        // 加密密码
        String encryptPwd = this.getEncryptPassword(userPassword);
        // 找用户信息
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",encryptPwd);

        User user = this.baseMapper.selectOne(queryWrapper);

        if(user == null){
            log.info("User login failed.\nAcc:\t"+userAccount+"\npwd:\t" + userPassword+"\nencryptPwd:\t"+encryptPwd);
            throw new BussinessException(ErrorCode.PARAMS_ERROR,"登录失败,请检查输入信息");
        }


        // 保存信息
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATUS,user);
        return this.getLoginUserVO(user);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 判断是否已登录
        User user =(User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATUS);
        ThrowUtils.throwIf(
                user == null || user.getId() == null,
                ErrorCode.NOT_LOGIN_ERROR
        );
        user = this.getById(user.getId());
        ThrowUtils.throwIf(
                user == null ,
                ErrorCode.NOT_LOGIN_ERROR
        );
        return user;
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        // 判断是否已登录
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATUS);
        ThrowUtils.throwIf(
                userObj == null,
                ErrorCode.OPERATION_ERROR,"未登录"
        );
        // 移除登录态
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATUS);

        return true;
    }

    /**
     * @param user 数据库获取的对象
     * @return 脱敏后用户对象
     */
    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if(user == null) return null;
        LoginUserVO vo = new LoginUserVO();
        BeanUtils.copyProperties(user,vo);
        return vo;
    }

    /**
     * 获取脱敏后的用户对象
     * @param user 数据库获取的对象
     * @return 脱敏对象
     */
    @Override
    public UserVO getUserVO(User user) {
        if(user == null) return null;
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user,vo);
        return vo;
    }

    /**
     * 获取脱敏后的用户列表
     * @param userList 数据库获取的对象列表
     * @return 脱敏对象列表
     */
    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if(CollUtil.isEmpty(userList)){
            return new ArrayList<>();
        }
        return userList.stream()
                .map(this::getUserVO)
                .collect(Collectors.toList());

    }

    /**
     * 获取查询条件
     * @param userQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {

        ThrowUtils.throwIf(
                userQueryRequest == null,
                ErrorCode.PARAMS_ERROR,"请求参数为空"
        );
        Long id = userQueryRequest.getId();
        String userName = userQueryRequest.getUserName();
        String userAccount = userQueryRequest.getUserAccount();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotEmpty(id),"id",id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userRole),"userRole",userRole);
        queryWrapper.like(StrUtil.isNotEmpty(userAccount),"userAccount",userAccount);
        queryWrapper.like(StrUtil.isNotEmpty(userName),"userName",userName);
        queryWrapper.like(StrUtil.isNotEmpty(userProfile),"userProfile",userProfile);
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField),sortOrder.equals("ascend"),sortField);

        return queryWrapper;
    }

    /**
     * 获取到加密后的密码
     * @param userPassword 加密前的密码
     * @return 加密后的密码
     */
    @Override
    public String getEncryptPassword(String userPassword){
        final String salt = "jc_PPPic"; // 盐值
        return DigestUtils.md5DigestAsHex((salt + userPassword).getBytes());
    }


}





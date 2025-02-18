package com.jcgg.jcpic_backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jcgg.jcpic_backend.annotation.AuthCheck;
import com.jcgg.jcpic_backend.common.BaseResponse;
import com.jcgg.jcpic_backend.common.DeleteRequest;
import com.jcgg.jcpic_backend.common.ResultUtils;
import com.jcgg.jcpic_backend.constant.UserConstant;
import com.jcgg.jcpic_backend.exception.ErrorCode;
import com.jcgg.jcpic_backend.exception.ThrowUtils;
import com.jcgg.jcpic_backend.model.dto.user.*;
import com.jcgg.jcpic_backend.model.entity.User;
import com.jcgg.jcpic_backend.model.vo.LoginUserVO;
import com.jcgg.jcpic_backend.model.vo.UserVO;
import com.jcgg.jcpic_backend.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController // 接口类
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    // region 注册、登录、退出登录

    /**
     * 用户注册
     *
     * @param userRegisterRequest 注册请求
     * @return 用户ID
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        ThrowUtils.throwIf(
                userRegisterRequest == null,
                ErrorCode.PARAMS_ERROR
        );
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        // ID
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录请求
     * @param request          获取Session ID
     * @return 脱敏后的用户信息
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(
                userLoginRequest == null,
                ErrorCode.PARAMS_ERROR
        );
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        // ID
        LoginUserVO loginVO = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(loginVO);
    }

    /**
     * 用户退出登录
     *
     * @param request 获取 Session
     * @return 是否成功退出登录
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        // ID
        boolean flag = userService.userLogout(request);
        return ResultUtils.success(flag);
    }

    // endregion

    /**
     * 获取当前登录用户
     *
     * @param request 获取请求
     * @return 返回脱敏后的登录用户信息
     */

    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {// 项目健康检查
        // 数据库查询到的未脱敏信息
        User user = userService.getLoginUser(request);
        // 将脱敏后的信息返回
        return ResultUtils.success(userService.getLoginUserVO(user));
    }


    // region CRUD

    /**
     * 用户添加
     * @param userAddRequest 用户添加请求
     * @return ID
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> userAdd(@RequestBody UserAddRequest userAddRequest) {
        ThrowUtils.throwIf(
                userAddRequest == null,
                ErrorCode.PARAMS_ERROR
        );
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        // 填充默认密码
        final String DEFAULT_PWD = "12345678";
        String encryptPwd = userService.getEncryptPassword(DEFAULT_PWD);
        user.setUserPassword(encryptPwd);

        boolean result = userService.save(user);
        ThrowUtils.throwIf(
                !result,
                ErrorCode.OPERATION_ERROR
        );
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取 未脱敏 用户信息(仅限管理员权限)
     *
     * @param id 用户id
     * @return 未脱敏 用户信息
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(Long id) {
        ThrowUtils.throwIf(
                id == null || id <= 0,
                ErrorCode.PARAMS_ERROR
        );
        User user = userService.getById(id);
        ThrowUtils.throwIf(
                user == null,
                ErrorCode.NOT_FOUND_ERROR
        );
        return ResultUtils.success(user);
    }

    /**
     * 根据 id 获取包装类
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(Long id) {
        BaseResponse<User> response = getUserById(id);
        User user = response.getData();
        return ResultUtils.success(userService.getUserVO(user));
    }

    /**
     * 删除用户
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(
                deleteRequest == null || deleteRequest.getId() <= 0,
                ErrorCode.PARAMS_ERROR
        );
        boolean b = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新用户
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        ThrowUtils.throwIf(
                userUpdateRequest == null || userUpdateRequest.getId() == null,
                ErrorCode.PARAMS_ERROR
        );

        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion

    /**
     * 分页查询_用户列表
     * 仅限管理员权限
     *
     * @param queryRequest 查询请求
     * @return 脱敏后的用户列表
     */
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest queryRequest) {
        ThrowUtils.throwIf(
                queryRequest == null,
                ErrorCode.PARAMS_ERROR
        );
        long current = queryRequest.getCurrent();
        long pageSize = queryRequest.getPageSize();

        Page<User> userPage = userService.page(
                new Page<>(current, pageSize),
                userService.getQueryWrapper(queryRequest)
        );

        Page<UserVO> voPage = new Page<>(current, pageSize, userPage.getTotal());
        List<UserVO> userVOList = userService.getUserVOList(userPage.getRecords());
        voPage.setRecords(userVOList);

        return ResultUtils.success(voPage);
    }
}

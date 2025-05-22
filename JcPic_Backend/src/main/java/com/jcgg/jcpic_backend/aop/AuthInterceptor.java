package com.jcgg.jcpic_backend.aop;

import com.jcgg.jcpic_backend.annotation.AuthCheck;
import com.jcgg.jcpic_backend.exception.ErrorCode;
import com.jcgg.jcpic_backend.exception.ThrowUtils;
import com.jcgg.jcpic_backend.model.entity.User;
import com.jcgg.jcpic_backend.model.enums.UserRoleEnum;
import com.jcgg.jcpic_backend.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


/**
 * 权限校验
 */
@Aspect
@Component
public class AuthInterceptor {
    @Resource
    private UserService userService;

    /**
     * 执行拦截
     *
     * @param joinPoint 切点
     * @param authCheck 权限校验注释
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        String mustRole = authCheck.mustRole();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);

        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
        // 不需要权限
        if (mustRoleEnum == null) {
            return joinPoint.proceed();
        }

        // 至少需要以下权限
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());
        ThrowUtils.throwIf(userRoleEnum == null, ErrorCode.NO_AUTH_ERROR);

        // 要求管理员权限, 但是没有管理员权限
        ThrowUtils.throwIf(
                UserRoleEnum.ADMIN.equals(mustRoleEnum) && !UserRoleEnum.ADMIN.equals(userRoleEnum),
                ErrorCode.NO_AUTH_ERROR
        );

        // 要求会员权限,但是没有会员或者管理员权限
        ThrowUtils.throwIf(
                UserRoleEnum.VIP.equals(mustRoleEnum) && (
                        UserRoleEnum.ADMIN.equals(userRoleEnum) || UserRoleEnum.VIP.equals(userRoleEnum)),
                ErrorCode.NO_AUTH_ERROR
        );
        return joinPoint.proceed();
    }

}

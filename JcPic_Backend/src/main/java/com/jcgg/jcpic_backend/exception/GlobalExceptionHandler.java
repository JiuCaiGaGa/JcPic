package com.jcgg.jcpic_backend.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import com.jcgg.jcpic_backend.common.BaseResponse;
import com.jcgg.jcpic_backend.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // 环绕切面
@Slf4j // 日志注解
public class GlobalExceptionHandler { // 切点
    @ExceptionHandler(NotLoginException.class)
    public BaseResponse<?> notLoginException(NotLoginException e) {
        log.error("NotLoginException", e);
        return ResultUtils.error(ErrorCode.NOT_LOGIN_ERROR, e.getMessage());
    }

    @ExceptionHandler(NotPermissionException.class)
    public BaseResponse<?> notPermissionExceptionHandler(NotPermissionException e) {
        log.error("NotPermissionException", e);
        return ResultUtils.error(ErrorCode.NO_AUTH_ERROR, e.getMessage());
    }

    /**
     * 针对自定义异常的捕获
     * @param e 自定义异常
     */
    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e){
        log.info("BusinessException",e);// 控制台打印日志
        return ResultUtils.error(e.getCode(),e.getMessage());
    }

    /**
     * 针对系统异常的捕获
     * @param e 系统异常
     */
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> businessExceptionHandler(RuntimeException e){
        log.info("RuntimeException",e);// 控制台打印日志
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR,"系统错误");
    }
}

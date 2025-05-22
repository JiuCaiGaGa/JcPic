package com.jcgg.jcpic_backend.exception;


/*
*  异常处理类
* */
public class ThrowUtils {

    /**
     * 异常抛出
     *
     * @param condition 异常抛出条件
     * @param runtimeException
     */
    public static void throwIf(boolean condition, RuntimeException runtimeException){
        if(condition){
            throw runtimeException;
        }
    };

    /**
     * 异常抛出
     *
     * @param condition 异常抛出条件
     * @param errorCode 错误码
     */
    public static void throwIf(boolean condition, ErrorCode errorCode){
        throwIf(condition,new BusinessException(errorCode));
    };

    /**
     *
     * @param condition 异常抛出条件
     * @param errorCode 错误码
     * @param message 异常抛出自定义消息
     */
    public static void throwIf(boolean condition, ErrorCode errorCode, String message){
        throwIf(condition,new BusinessException(errorCode,message));
    };
}

package com.jcgg.jcpic_backend.common;

import com.jcgg.jcpic_backend.exception.ErrorCode;
import lombok.Data;

import java.io.Serializable;
/**
 * 全局通用响应封装类
 * @param <T> 泛型
 */
@Data
public class BaseResponse<T> implements Serializable {
    private int code;
    private T data;
    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data){
        this(code,data,"");
    }

    public BaseResponse(ErrorCode errorCode){
        this(errorCode.getCode(),null,errorCode.getMessage());
    }
}

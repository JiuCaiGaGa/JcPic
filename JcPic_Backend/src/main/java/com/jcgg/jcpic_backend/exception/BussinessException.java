package com.jcgg.jcpic_backend.exception;

import lombok.Getter;



/*
* 自定义异常
* 异常详情见 ErrorCode 类
* */
@Getter
public class BussinessException extends RuntimeException {

    // 错误码
    private final int code;

    public BussinessException(int code, String message) {
        super(message);
        this.code = code;
    }
    public BussinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BussinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }
}

package com.jcgg.jcpic_backend.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)// 对方法生效
@Retention(RetentionPolicy.RUNTIME) // 运行时生效
public @interface AuthCheck {

    /**
     * 必须是某种角色
     */
    String mustRole() default "";
}

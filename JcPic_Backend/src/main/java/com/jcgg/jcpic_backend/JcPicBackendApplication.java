package com.jcgg.jcpic_backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.jcgg.jcpic_backend.mapper") // mapper 扫描文件夹
@EnableAspectJAutoProxy(exposeProxy = true) // 暴露代理
public class JcPicBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(JcPicBackendApplication.class, args);
    }

}

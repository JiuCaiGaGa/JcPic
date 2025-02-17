package com.jcgg.jcpic_backend.controller;

import com.jcgg.jcpic_backend.common.BaseResponse;
import com.jcgg.jcpic_backend.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // 接口类
@RequestMapping("/")
public class MainController {
    /**
     * 健康检查用接口
     */
    @GetMapping("/health")
    public BaseResponse<String> health(){// 项目健康检查
        return ResultUtils.success("ok");
    }
}
